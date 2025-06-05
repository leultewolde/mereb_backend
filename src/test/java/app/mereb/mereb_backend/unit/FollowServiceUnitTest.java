package app.mereb.mereb_backend.unit;

import app.mereb.mereb_backend.follow.*;
import app.mereb.mereb_backend.user.User;
import app.mereb.mereb_backend.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FollowServiceUnitTest {

    @Mock private FollowRepository followRepo;
    @Mock private UserRepository userRepo;
    @InjectMocks private FollowService followService;

    @Test
    void shouldFollowUser() {
        UUID followerId = UUID.randomUUID();
        UUID followingId = UUID.randomUUID();
        String username = "target";

        User followingUser = new User();
        followingUser.setId(followingId);
        followingUser.setUsername(username);

        when(userRepo.findByUsername(username)).thenReturn(Optional.of(followingUser));

        followService.follow(followerId, username);

        verify(userRepo).findByUsername(username);
        verify(followRepo).save(any(Follow.class));
    }

    @Test
    void shouldNotFollowSelf() {
        UUID sameId = UUID.randomUUID();
        String username = "self";

        User user = new User();
        user.setId(sameId);
        user.setUsername(username);

        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));

        followService.follow(sameId, username);

        verify(userRepo).findByUsername(username);
        verify(followRepo, never()).save(any());
    }

    @Test
    void shouldUnfollowUser() {
        UUID followerId = UUID.randomUUID();
        UUID followingId = UUID.randomUUID();
        String username = "john";

        User user = new User();
        user.setId(followingId);

        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));

        followService.unfollow(followerId, username);

        verify(userRepo).findByUsername(username);
        verify(followRepo).deleteById(new FollowId(followerId, followingId));
    }

    @Test
    void shouldCheckIfUserIsFollowing() {
        UUID followerId = UUID.randomUUID();
        UUID followingId = UUID.randomUUID();
        String username = "target";

        User user = new User();
        user.setId(followingId);

        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(followRepo.existsByFollowerIdAndFollowingId(followerId, followingId)).thenReturn(true);

        boolean result = followService.isFollowing(followerId, username);

        assertTrue(result);
        verify(userRepo).findByUsername(username);
        verify(followRepo).existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    @Test
    void shouldReturnFollowersCount() {
        UUID userId = UUID.randomUUID();
        String username = "john";

        User user = new User();
        user.setId(userId);

        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(followRepo.countByFollowingId(userId)).thenReturn(5);

        long count = followService.getFollowersCount(username);

        assertEquals(5, count);
        verify(userRepo).findByUsername(username);
        verify(followRepo).countByFollowingId(userId);
    }

    @Test
    void shouldReturnFollowingCount() {
        UUID userId = UUID.randomUUID();
        String username = "jane";

        User user = new User();
        user.setId(userId);

        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(followRepo.countByFollowerId(userId)).thenReturn(3);

        long count = followService.getFollowingCount(username);

        assertEquals(3, count);
        verify(userRepo).findByUsername(username);
        verify(followRepo).countByFollowerId(userId);
    }
}
