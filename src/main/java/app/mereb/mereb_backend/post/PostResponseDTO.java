package app.mereb.mereb_backend.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDTO {
    private UUID id;
    private String content;
    private String authorUsername;
    private PostType type;
    private UUID repostOf_postID;
    private String repostOf_authorUsername;
    private String repostOf_content;
    private Instant repostOf_createdAt;
    private Instant repostOf_updatedAt;
    private boolean isHidden;
    private Instant createdAt;
    private Instant updatedAt;

    private boolean likedByCurrentUser;
    private int likeCount;

    private List<String> allReposters;

    private boolean repostedByCurrentUser;
    private int repostCount;
}

