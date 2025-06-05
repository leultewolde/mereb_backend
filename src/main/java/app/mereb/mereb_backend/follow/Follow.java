package app.mereb.mereb_backend.follow;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "follows")
@IdClass(FollowId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Follow {
    @Id
    @NotNull
    private UUID followerId;
    @Id
    @NotNull
    private UUID followingId;
}

