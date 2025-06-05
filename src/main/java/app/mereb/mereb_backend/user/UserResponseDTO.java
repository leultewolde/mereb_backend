package app.mereb.mereb_backend.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private UUID id;
    private String email;
    private String phone;
    private String username;
    private Role role;
    private boolean isSuspended;
    private Instant createdAt;
    private Instant updatedAt;
}
