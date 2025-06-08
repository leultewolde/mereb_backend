package app.mereb.mereb_backend.config;

import app.mereb.mereb_backend.auth.JwtUtil;
import app.mereb.mereb_backend.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class TestableJwtFilter extends JwtAuthenticationFilter {
    public TestableJwtFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        super(jwtUtil, userRepository);
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        super.doFilterInternal(request, response, chain);
    }
}
