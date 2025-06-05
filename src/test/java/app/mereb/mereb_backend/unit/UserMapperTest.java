package app.mereb.mereb_backend.unit;

import app.mereb.mereb_backend.user.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void shouldMapUserToDto() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("john");
        user.setSuspended(false);
        user.setRole(Role.USER);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        UserResponseDTO dto = userMapper.toDto(user);

        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getUsername(), dto.getUsername());
        assertEquals(user.getRole(), dto.getRole());
    }

    @Test
    void shouldMapUserListToDtoList() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("john");

        List<UserResponseDTO> result = userMapper.toDtoList(List.of(user));

        assertEquals(1, result.size());
        assertEquals("john", result.get(0).getUsername());
    }
}
