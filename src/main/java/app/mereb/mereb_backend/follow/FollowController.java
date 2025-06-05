package app.mereb.mereb_backend.follow;

import app.mereb.mereb_backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/follow")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @PostMapping("/{username}")
    public ResponseEntity<?> follow(Authentication auth, @PathVariable String username) {
        User user = (User) auth.getPrincipal();
        followService.follow(user.getId(), username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> unfollow(Authentication auth, @PathVariable String username) {
        User user = (User) auth.getPrincipal();
        followService.unfollow(user.getId(), username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/is-following/{username}")
    public boolean isFollowing(Authentication auth, @PathVariable String username) {
        User currentUser = (User) auth.getPrincipal();
        return followService.isFollowing(currentUser.getId(), username);
    }

    @GetMapping("/{username}/followers/count")
    public long getFollowersCount(@PathVariable String username) {
        return followService.getFollowersCount(username);
    }

    @GetMapping("/{username}/following/count")
    public long getFollowingCount(@PathVariable String username) {
        return followService.getFollowingCount(username);
    }
}
