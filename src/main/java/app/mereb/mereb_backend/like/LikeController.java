package app.mereb.mereb_backend.like;

import app.mereb.mereb_backend.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/likes")
@RequiredArgsConstructor
@Slf4j
public class LikeController {
    private final LikeRepository likeRepo;

    @PostMapping("/{postId}")
    public ResponseEntity<?> likePost(Authentication auth, @PathVariable UUID postId) {
        User user = (User) auth.getPrincipal();
        if (!likeRepo.existsByUserIdAndPostId(user.getId(), postId)) {
            Like like = new Like();
            like.setUserId(user.getId());
            like.setPostId(postId);
            likeRepo.save(like);
            log.info("User {} liked post {}", user.getId(), postId);
            return ResponseEntity.ok().build();
        } else {
            log.warn("User {} tried to like post {} again", user.getId(), postId);
            return ResponseEntity.status(409).body("Already liked");
        }
    }

    @DeleteMapping("/{postId}")
    @Transactional
    public ResponseEntity<?> unlikePost(Authentication auth, @PathVariable UUID postId) {
        User user = (User) auth.getPrincipal();
        if (likeRepo.existsByUserIdAndPostId(user.getId(), postId)) {
            likeRepo.deleteByUserIdAndPostId(user.getId(), postId);
            log.info("User {} unliked post {}", user.getId(), postId);
            return ResponseEntity.ok().build();
        } else {
            log.warn("User {} tried to unlike post {} that was not liked", user.getId(), postId);
            return ResponseEntity.status(404).body("Like not found");
        }
    }
}