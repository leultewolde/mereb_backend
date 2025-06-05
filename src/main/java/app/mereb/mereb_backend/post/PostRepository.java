package app.mereb.mereb_backend.post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

// PostRepository.java
public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findTop20ByUserIdInAndIsHiddenFalseOrderByCreatedAtDesc(List<UUID> userIds);
    List<Post> findTop20ByUserIdInOrIdInAndIsHiddenFalseOrderByCreatedAtDesc(
            List<UUID> userIds, List<UUID> repostIds
    );
    List<Post> findTop20ByIsHiddenFalseOrderByCreatedAtDesc();
    List<Post> findTop20ByUserIdOrderByCreatedAtDesc(UUID id);

    List<Post> findTop20ByRepostOf(UUID id);

    boolean existsByRepostOfAndUserId(UUID repostId, UUID userId);


    List<Post> findAllByRepostOfInAndUserId(Set<UUID> postIds, UUID userId);
    List<Post> findAllByRepostOfIn(Set<UUID> postIds);
}
