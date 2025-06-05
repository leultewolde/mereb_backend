package app.mereb.mereb_backend.post;

import app.mereb.mereb_backend.follow.FollowRepository;
import app.mereb.mereb_backend.like.Like;
import app.mereb.mereb_backend.like.LikeRepository;
import app.mereb.mereb_backend.user.User;
import app.mereb.mereb_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final FollowRepository followRepo;
    private final UserRepository userRepo;
    private final PostRepository postRepo;
    private final LikeRepository likeRepo;
    private final PostMapper postMapper;

    public PostResponseDTO createPost(User user, PostCreateRequestDTO requestDTO) {
        Post post = new Post();
        post.setId(UUID.randomUUID());
        post.setUserId(user.getId());
        post.setContent(requestDTO.getContent());
        return postMapper.toDto(postRepo.save(post));
    }

    public List<PostResponseDTO> getFeed(User user) {
        List<UUID> followingIds = followRepo.findFollowingIdsByUserId(user.getId());

        List<Post> postList = followingIds.isEmpty()
                ? postRepo.findTop20ByIsHiddenFalseOrderByCreatedAtDesc()
                : postRepo.findTop20ByUserIdInAndIsHiddenFalseOrderByCreatedAtDesc(followingIds);

        return enrichAndSortPosts(postList, user);
    }

    public List<PostResponseDTO> getPostsByUsername(String username) {
        User user = userRepo.findByUsername(username).orElseThrow();
        List<Post> postList = postRepo.findTop20ByUserIdOrderByCreatedAtDesc(user.getId());
        log.info("posts: {}", postList);
        return enrichAndSortPosts(postList, user);
    }

    public List<Post> getAllPosts() {
        return postRepo.findAll();
    }

    public void setVisibility(UUID id, boolean hidden) {
        postRepo.findById(id).ifPresent(p -> {
            p.setHidden(hidden);
            postRepo.save(p);
        });
    }

    public void deletePost(UUID id) {
        postRepo.deleteById(id);
    }

    public void hidePost(UUID id) {
        // isHidden = true means not visible
        setVisibility(id, true);
    }

    public void showPost(UUID id) {
        // isHidden = false means visible
        setVisibility(id, false);
    }


    private List<PostResponseDTO> enrichAndSortPosts(List<Post> posts, User user) {
        return enrichPostDTOs(posts, user).stream()
                .sorted(Comparator.comparing(PostResponseDTO::getCreatedAt).reversed())
                .toList();
    }

    private List<PostResponseDTO> enrichPostDTOs(List<Post> posts, User currentUser) {
        UUID currentUserId = currentUser.getId();
        List<PostResponseDTO> dtoList = posts.stream().map(postMapper::toDto).toList();

        log.info("Posts {}", posts);

        // 1. Collect all userIds + repostOf postIds to batch-fetch users/posts
        Set<UUID> userIds = new HashSet<>();
        Set<UUID> repostPostIds = new HashSet<>();
        Set<UUID> postIds = new HashSet<>();

        for (Post post : posts) {
            userIds.add(post.getUserId());
            postIds.add(post.getId());
            if (post.getRepostOf() != null) repostPostIds.add(post.getRepostOf());
        }

        Map<UUID, User> users = userRepo.findAllById(userIds).stream().collect(Collectors.toMap(User::getId, u -> u));
        Map<UUID, Post> repostPosts = postRepo.findAllById(repostPostIds).stream().collect(Collectors.toMap(Post::getId, p -> p));

        // Optional: batch likes and reposts per user
        Set<UUID> likedPosts = likeRepo.findAllByUserIdAndPostIdIn(currentUserId, postIds).stream()
                .map(Like::getPostId).collect(Collectors.toSet());

        Set<UUID> repostedPosts = postRepo.findAllByRepostOfInAndUserId(postIds, currentUserId).stream()
                .map(Post::getRepostOf).collect(Collectors.toSet());

        Map<UUID, Integer> likeCounts = likeRepo.countAllByPostIdIn(postIds); // custom query: GROUP BY postId
        Map<UUID, List<User>> repostersMap = postRepo.findAllByRepostOfIn(postIds).stream()
                .collect(Collectors.groupingBy(Post::getRepostOf,
                        Collectors.mapping(p -> users.get(p.getUserId()), Collectors.toList())));

        // Fill each DTO efficiently
        for (PostResponseDTO dto : dtoList) {
//            Post post = posts.stream().filter(p -> p.getId().equals(dto.getId())).findFirst().orElse(null);
            Map<UUID, Post> postMap = posts.stream().collect(Collectors.toMap(Post::getId, p -> p));
            Post post = postMap.get(dto.getId());
            if (post == null) continue;

            User author = users.get(post.getUserId());
            if (author != null) dto.setAuthorUsername(author.getUsername());

            if (post.getRepostOf() != null) {
                Post original = repostPosts.get(post.getRepostOf());
                if (original != null) {
                    dto.setRepostOf_postID(original.getId());
                    dto.setRepostOf_content(original.getContent());
                    dto.setRepostOf_createdAt(original.getCreatedAt());
                    dto.setRepostOf_updatedAt(original.getUpdatedAt());
                    User ogUser = users.get(original.getUserId());
                    if (ogUser != null) dto.setRepostOf_authorUsername(ogUser.getUsername());
                }
            }

            List<User> reposters = repostersMap.getOrDefault(post.getId(), List.of());
            dto.setAllReposters(reposters.stream().map(User::getUsername).toList());
            dto.setRepostCount(reposters.size());

            dto.setLikedByCurrentUser(currentUserId != null && likedPosts.contains(post.getId()));
            dto.setRepostedByCurrentUser(currentUserId != null && repostedPosts.contains(post.getId()));
            dto.setLikeCount(likeCounts.getOrDefault(post.getId(), 0));
        }
        log.info("Post dtos {}", dtoList);
        return dtoList;
    }


}
