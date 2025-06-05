package app.mereb.mereb_backend.auth;

import app.mereb.mereb_backend.exception.EntityNotFoundException;
import app.mereb.mereb_backend.exception.WrongPasswordException;
import app.mereb.mereb_backend.user.User;
import app.mereb.mereb_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) throw new RuntimeException("Username exists");
        User user = new User(UUID.randomUUID(), req.getEmail(), req.getPhone(), req.getUsername(),
                new BCryptPasswordEncoder().encode(req.getPassword()));
        userRepo.save(user);
        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User"));
        if (!new BCryptPasswordEncoder().matches(req.getPassword(), user.getPasswordHash()))
            throw new WrongPasswordException();
        return new AuthResponse(jwtUtil.generateToken(user));
    }
}
