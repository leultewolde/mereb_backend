package app.mereb.mereb_backend.post;

import app.mereb.mereb_backend.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(Authentication auth, @Valid @RequestBody PostCreateRequestDTO requestDTO) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity
                .created(URI.create("/v1/posts"))
                .body(postService.createPost(user, requestDTO));
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostResponseDTO>> getFeed(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(postService.getFeed(user));
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<List<PostResponseDTO>> getPostsByUsername(@PathVariable String username) {
        return ResponseEntity.ok(postService.getPostsByUsername(username));
    }
}

