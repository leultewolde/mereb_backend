package app.mereb.mereb_backend.contact;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contacts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contact {
    @Id
    @NotNull
    private UUID id;
    @NotNull
    private UUID userId;
    @NotBlank
    @Size(max = 20)
    private String phoneNumber;
    @NotNull
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant createdAt = Instant.now();
}

