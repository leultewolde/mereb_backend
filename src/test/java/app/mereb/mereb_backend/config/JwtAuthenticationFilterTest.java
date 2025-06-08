package app.mereb.mereb_backend.config;

import static org.junit.jupiter.api.Assertions.*;

import app.mereb.mereb_backend.auth.JwtUtil;
import app.mereb.mereb_backend.user.User;
import app.mereb.mereb_backend.user.UserRepository;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtUtil jwtUtil;
    private UserRepository userRepository;
    private TestableJwtFilter filter;

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JwtUtil.class);
        userRepository = mock(UserRepository.class);
        filter = new TestableJwtFilter(jwtUtil, userRepository);
    }

    @Test
    void testValidTokenSetsAuthentication() throws Exception {
        UUID userId = UUID.randomUUID();
        String token = "valid.token";
        when(jwtUtil.validateToken(token)).thenReturn(userId.toString());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertEquals(200, response.getStatus());
    }

    @Test
    void testInvalidTokenReturnsUnauthorized() throws Exception {
        String token = "invalid.token";
        when(jwtUtil.validateToken(token)).thenThrow(new RuntimeException("Invalid token"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setCharacterEncoding("UTF-8");
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Unauthorized"));
    }

    @Test
    void testNoAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
    }
}

