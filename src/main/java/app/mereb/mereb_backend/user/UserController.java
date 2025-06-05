package app.mereb.mereb_backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getProfile(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
        UserResponseDTO user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateProfile(Authentication auth, @RequestBody Map<String, String> updates) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(userService.updateProfile(user, updates));
    }

    @DeleteMapping("/me")
    public ResponseEntity<String> deleteUser(Authentication auth) {
        User user = (User) auth.getPrincipal();
        ;
        return ResponseEntity.ok(userService.deleteUser(user));
    }
}

