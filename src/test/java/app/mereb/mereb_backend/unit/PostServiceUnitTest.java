package app.mereb.mereb_backend.unit;

import app.mereb.mereb_backend.follow.FollowRepository;
import app.mereb.mereb_backend.like.LikeRepository;
import app.mereb.mereb_backend.post.*;
import app.mereb.mereb_backend.user.User;
import app.mereb.mereb_backend.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceUnitTest {

    @Mock private FollowRepository followRepo;
    @Mock private UserRepository userRepo;
    @Mock private PostRepository postRepo;
    @Mock private LikeRepository likeRepo;
    @Mock private PostMapper postMapper;

    @InjectMocks private PostService postService;

    @Test
    void shouldCreatePost() {
        User user = new User();
        user.setId(UUID.randomUUID());

        Post post = new Post();
        post.setId(UUID.randomUUID());
        post.setUserId(user.getId());
        post.setContent("Hello");

        PostResponseDTO dto = new PostResponseDTO();
        dto.setContent("Hello");

        when(postRepo.save(any(Post.class))).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(dto);

        PostResponseDTO result = postService.createPost(user, new PostCreateRequestDTO("Hello"));

        assertNotNull(result);
        assertEquals("Hello", result.getContent());

        verify(postRepo).save(any(Post.class));
        verify(postMapper).toDto(post);
    }

    @Test
    void shouldReturnFeedWithFallbackPostsWhenFollowingIsEmpty() {
        User user = new User();
        user.setId(UUID.randomUUID());

        List<Post> fallbackPosts = List.of(new Post());
        when(followRepo.findFollowingIdsByUserId(user.getId())).thenReturn(Collections.emptyList());
        when(postRepo.findTop20ByIsHiddenFalseOrderByCreatedAtDesc()).thenReturn(fallbackPosts);
        when(postMapper.toDto(any(Post.class))).thenReturn(new PostResponseDTO());
        when(userRepo.findAllById(any())).thenReturn(List.of());
        when(postRepo.findAllById(any())).thenReturn(List.of());
        when(likeRepo.findAllByUserIdAndPostIdIn(any(), any())).thenReturn(List.of());
        when(postRepo.findAllByRepostOfInAndUserId(any(), any())).thenReturn(List.of());
        when(likeRepo.countAllByPostIdIn(any())).thenReturn(new HashMap<>());
        when(postRepo.findAllByRepostOfIn(any())).thenReturn(List.of());

        List<PostResponseDTO> result = postService.getFeed(user);

        assertNotNull(result);
        verify(followRepo).findFollowingIdsByUserId(user.getId());
        verify(postRepo).findTop20ByIsHiddenFalseOrderByCreatedAtDesc();
    }

    @Test
    void shouldGetPostsByUsername() {
        String username = "john";
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setUsername(username);

        List<Post> posts = List.of(new Post());

        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(postRepo.findTop20ByUserIdOrderByCreatedAtDesc(userId)).thenReturn(posts);
        when(postMapper.toDto(any(Post.class))).thenReturn(new PostResponseDTO());
        when(userRepo.findAllById(any())).thenReturn(List.of(user));
        when(postRepo.findAllById(any())).thenReturn(List.of());
        when(likeRepo.findAllByUserIdAndPostIdIn(any(), any())).thenReturn(List.of());
        when(postRepo.findAllByRepostOfInAndUserId(any(), any())).thenReturn(List.of());
        when(likeRepo.countAllByPostIdIn(any())).thenReturn(new HashMap<>());
        when(postRepo.findAllByRepostOfIn(any())).thenReturn(List.of());

        List<PostResponseDTO> result = postService.getPostsByUsername(username);

        assertNotNull(result);
        verify(userRepo).findByUsername(username);
        verify(postRepo).findTop20ByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    void shouldGetAllPosts() {
        List<Post> posts = List.of(new Post());
        when(postRepo.findAll()).thenReturn(posts);

        List<Post> result = postService.getAllPosts();

        assertEquals(1, result.size());
        verify(postRepo).findAll();
    }

    @Test
    void shouldHidePost() {
        UUID postId = UUID.randomUUID();
        Post post = new Post();
        post.setId(postId);

        when(postRepo.findById(postId)).thenReturn(Optional.of(post));
        when(postRepo.save(post)).thenReturn(post);

        postService.hidePost(postId);

        assertTrue(post.isHidden());
        verify(postRepo).findById(postId);
        verify(postRepo).save(post);
    }

    @Test
    void shouldShowPost() {
        UUID postId = UUID.randomUUID();
        Post post = new Post();
        post.setId(postId);
        post.setHidden(true);

        when(postRepo.findById(postId)).thenReturn(Optional.of(post));
        when(postRepo.save(post)).thenReturn(post);

        postService.showPost(postId);

        assertFalse(post.isHidden());
        verify(postRepo).findById(postId);
        verify(postRepo).save(post);
    }

    @Test
    void shouldDeletePost() {
        UUID postId = UUID.randomUUID();
        doNothing().when(postRepo).deleteById(postId);

        postService.deletePost(postId);

        verify(postRepo).deleteById(postId);
    }
}
