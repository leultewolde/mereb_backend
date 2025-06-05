package app.mereb.mereb_backend.admin;

import app.mereb.mereb_backend.post.Post;
import app.mereb.mereb_backend.post.PostService;
import app.mereb.mereb_backend.user.UserResponseDTO;
import app.mereb.mereb_backend.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final PostService postService;

    @GetMapping("/users")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @PatchMapping("/users/{id}/suspend")
    public ResponseEntity<UserResponseDTO> suspendUser(@PathVariable UUID id, @RequestParam String suspend) {
        UserResponseDTO dto = userService.updateSuspended(id, Boolean.parseBoolean(suspend));
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/posts")
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @PatchMapping("/posts/{id}/visibility")
    public ResponseEntity<?> toggleVisibility(@PathVariable UUID id, @RequestBody Map<String, Boolean> body) {
        postService.setVisibility(id, body.get("hidden"));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }
}
