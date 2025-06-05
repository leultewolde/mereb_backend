package app.mereb.mereb_backend.like;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeId implements Serializable {
    private UUID userId;
    private UUID postId;
}
