package app.mereb.mereb_backend.follow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {
    @Query("SELECT f.followingId FROM Follow f WHERE f.followerId = :userId")
    List<UUID> findFollowingIdsByUserId(@Param("userId") UUID userId);
    boolean existsByFollowerIdAndFollowingId(UUID followerId, UUID followingId);
    int countByFollowerId(UUID followerId);
    int countByFollowingId(UUID followingId);
}
