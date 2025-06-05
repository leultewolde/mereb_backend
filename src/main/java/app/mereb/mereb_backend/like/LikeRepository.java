package app.mereb.mereb_backend.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface LikeRepository extends JpaRepository<Like, LikeId> {
    boolean existsByUserIdAndPostId(UUID userId, UUID postId);
    void deleteByUserIdAndPostId(UUID userId, UUID postId);
    int countByUserIdAndPostId(UUID userId, UUID postId);
    int countByPostId(UUID postId);

    List<Like> findAllByUserIdAndPostIdIn(UUID userId, Set<UUID> postIds);
    @Query("SELECT l.postId, COUNT(l) FROM Like l WHERE l.postId IN :postIds GROUP BY l.postId")
    Map<UUID, Integer> countAllByPostIdIn(Set<UUID> postIds);
}
