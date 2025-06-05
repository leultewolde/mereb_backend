package app.mereb.mereb_backend.unit;

import app.mereb.mereb_backend.auth.JwtUtil;
import app.mereb.mereb_backend.user.Role;
import app.mereb.mereb_backend.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilUnitTest {

    private JwtUtil jwtUtil;
    private final String secret = "this_is_a_super_secure_and_long_secret_key_123456";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(secret);
    }

    @Test
    void shouldGenerateAndValidateToken() {
        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setUsername("john");
        user.setRole(Role.USER);

        String token = jwtUtil.generateToken(user);

        assertNotNull(token);
        String subject = jwtUtil.validateToken(token);

        assertEquals(userId.toString(), subject);
    }

    @Test
    void shouldThrowExceptionForInvalidToken() {
        String invalidToken = "invalid.token.value";

        assertThrows(Exception.class, () -> jwtUtil.validateToken(invalidToken));
    }

    @Test
    void shouldThrowExceptionForShortSecretKey() {
        String shortKey = "too_short_secret";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new JwtUtil(shortKey));
        assertEquals("JWT secret key must be at least 32 characters", exception.getMessage());
    }
}
