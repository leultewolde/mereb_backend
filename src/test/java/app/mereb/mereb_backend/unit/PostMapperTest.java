package app.mereb.mereb_backend.unit;

import app.mereb.mereb_backend.post.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PostMapperTest {

    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Test
    void shouldMapPostToDto() {
        UUID postId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Post post = new Post();
        post.setId(postId);
        post.setUserId(userId);
        post.setContent("Test content");
        post.setCreatedAt(Instant.now());

        PostResponseDTO dto = postMapper.toDto(post);

        assertEquals(postId, dto.getId());
        assertEquals("Test content", dto.getContent());
    }
}
