package app.mereb.mereb_backend.superadmin;

import app.mereb.mereb_backend.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/superadmins")
@PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
@RequiredArgsConstructor
public class SuperAdminController {
    private final UserService userService;

    @GetMapping
    public List<UserResponseDTO> getAllSuperAdmins() {
        return userService.getAllSuperAdmins();
    }

    @GetMapping("/admins")
    public List<UserResponseDTO> getAllAdmins() {
        return userService.getAllAdmins();
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<UserResponseDTO> updateRole(@PathVariable UUID id, @RequestParam String role) {
        UserResponseDTO updatedUser = userService.updateRole(id, Role.valueOf(role.toUpperCase()));
        return ResponseEntity.ok(updatedUser);
    }
}
