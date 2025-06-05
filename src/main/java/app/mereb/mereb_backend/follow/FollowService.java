package app.mereb.mereb_backend.follow;

import app.mereb.mereb_backend.user.User;
import app.mereb.mereb_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public void follow(UUID followerId, String username) {
        UUID followingId = findUserIDByUsername(username);
        if (!followerId.equals(followingId)) {
            Follow f = new Follow();
            f.setFollowerId(followerId);
            f.setFollowingId(followingId);
            followRepository.save(f);
        }
    }

    public void unfollow(UUID followerId, String username) {
        followRepository.deleteById(new FollowId(followerId, findUserIDByUsername(username)));
    }

    public boolean isFollowing(UUID followerId, String username) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, findUserIDByUsername(username));
    }

    private UUID findUserIDByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return user.getId();
    }

    public long getFollowersCount(String username) {
        return followRepository.countByFollowingId(findUserIDByUsername(username));
    }

    public long getFollowingCount(String username) {
        return followRepository.countByFollowerId(findUserIDByUsername(username));
    }
}
