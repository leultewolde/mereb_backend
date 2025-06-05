package app.mereb.mereb_backend.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private UUID id;
    @Email
    @NotBlank
    @Size(max = 255)
    private String email;
    @NotBlank
    @Size(min = 6, max = 20)
    private String phone;
    @NotBlank
    @Size(min = 6, max = 50)
    private String username;
    @NotBlank
    @Size(min = 8, max = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Role role = Role.USER;

    private boolean isSuspended;
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant createdAt = Instant.now();
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant updatedAt = Instant.now();

    public User(UUID id, String email, String phone, String username, String passwordHash) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public User(UUID id, String email, String phone, String username, String passwordHash, Role role) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }
}



