package app.mereb.mereb_backend.config;

import app.mereb.mereb_backend.auth.JwtUtil;
import app.mereb.mereb_backend.user.User;
import app.mereb.mereb_backend.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    private static final List<String> EXCLUDED_URLS = List.of(
            "/api/v1/auth",
            "/api/swagger-ui",
            "/api/v3/api-docs",
            "/actuator/health",
            "/actuator",
            "/api/actuator",
            "/api/actuator/health"
    );

    private void rejectUnauthorizedRequest(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
        String requestURI = request.getRequestURI();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
        log.warn("Unauthorized access to {}: {}", requestURI, message);
        log.info("Unauthorized access to {}: {}", requestURI, message);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("doFilterInternal called for: " + request.getRequestURI());
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setContentType("application/json");
//            response.getWriter().write("{\"error\":\"Unauthorized - no token\"}");
            rejectUnauthorizedRequest(request, response, "Unauthorized - no token");
            return;
        }

        String token = authHeader.substring(7);
        try {
            String userId = jwtUtil.validateToken(token);
            User user = userRepository.findById(UUID.fromString(userId)).orElse(null);
            if (user != null) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_" + user.getRole().name()
                                )
                        )
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                rejectUnauthorizedRequest(request, response, "Unauthorized - invalid user in token");
                return;
            }
        } catch (Exception e) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setContentType("application/json");
//            response.getWriter().write("{\"error\":\"Unauthorized - invalid token\"}");
            rejectUnauthorizedRequest(request, response, "Unauthorized - invalid token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        System.out.println("shouldNotFilter applied for: " +
                request.getRequestURI() + " => " +
                EXCLUDED_URLS.stream().anyMatch(uri -> request.getRequestURI().contains(uri)));
        return EXCLUDED_URLS.stream().anyMatch(uri -> request.getRequestURI().contains(uri));
    }
}
