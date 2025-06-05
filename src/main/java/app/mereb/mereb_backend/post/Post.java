package app.mereb.mereb_backend.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    private UUID id;
    @NotNull(message = "User Id (Author) should not be null")
    private UUID userId;

    @Size(max = 300, message = "Content must not exceed 300 characters")
    private String content;

    private UUID repostOf;

    @Enumerated(EnumType.STRING)
    private PostType type = PostType.TEXT;
    private boolean isHidden = false;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant createdAt = Instant.now();
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant updatedAt = Instant.now();
}

