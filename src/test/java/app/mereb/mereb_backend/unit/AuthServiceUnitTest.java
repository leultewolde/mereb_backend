package app.mereb.mereb_backend.unit;

import app.mereb.mereb_backend.auth.*;
import app.mereb.mereb_backend.user.User;
import app.mereb.mereb_backend.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceUnitTest {

    @Mock private UserRepository userRepo;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks private AuthService authService;

    @Test
    void shouldRegisterUser() {
        RegisterRequest req = new RegisterRequest("email@example.com", "1234567890", "john", "pass123");
        when(userRepo.existsByUsername("john")).thenReturn(false);
        when(userRepo.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(jwtUtil.generateToken(any(User.class))).thenReturn("mock-token");

        AuthResponse response = authService.register(req);

        assertNotNull(response);
        assertEquals("mock-token", response.getToken());

        verify(userRepo).existsByUsername("john");
        verify(userRepo).save(any(User.class));
        verify(jwtUtil).generateToken(any(User.class));
    }

    @Test
    void shouldThrowWhenUsernameExists() {
        RegisterRequest req = new RegisterRequest("email@example.com", "1234567890", "john", "pass123");
        when(userRepo.existsByUsername("john")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(req));
        assertEquals("Username exists", ex.getMessage());

        verify(userRepo).existsByUsername("john");
        verify(userRepo, never()).save(any());
    }

    @Test
    void shouldLoginUser() {
        String rawPassword = "pass123";
        String hashedPassword = new BCryptPasswordEncoder().encode(rawPassword);

        User user = new User();
        user.setUsername("john");
        user.setPasswordHash(hashedPassword);

        LoginRequest req = new LoginRequest(null, null,"john", rawPassword);

        when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user)).thenReturn("mock-token");

        AuthResponse response = authService.login(req);

        assertNotNull(response);
        assertEquals("mock-token", response.getToken());

        verify(userRepo).findByUsername("john");
        verify(jwtUtil).generateToken(user);
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        LoginRequest req = new LoginRequest(null, null, "unknown", "pass");
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(req));
        assertEquals("User not found", ex.getMessage());

        verify(userRepo).findByUsername("unknown");
    }

    @Test
    void shouldThrowWhenPasswordIsWrong() {
        String wrongPassword = "wrong";
        String hashedPassword = new BCryptPasswordEncoder().encode("correct");

        User user = new User();
        user.setUsername("john");
        user.setPasswordHash(hashedPassword);

        LoginRequest req = new LoginRequest(null, null, "john", wrongPassword);

        when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(req));
        assertEquals("Invalid Password", ex.getMessage());

        verify(userRepo).findByUsername("john");
    }
}
