package app.mereb.mereb_backend.component;

import app.mereb.mereb_backend.auth.JwtUtil;
import app.mereb.mereb_backend.user.Role;
import app.mereb.mereb_backend.user.User;
import app.mereb.mereb_backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class UserControllerComponentTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;

    private String userToken;
    private UUID userId;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("test_user")
                .role(Role.USER)
                .email("user@email.com")
                .phone("123456789")
                .passwordHash("userPassword")
                .build();

        userRepository.save(user);
        userId = user.getId();
        userToken = jwtUtil.generateToken(user);
    }

    @Test
    void shouldReturnOwnProfile() throws Exception {
        mockMvc.perform(get("/v1/users/me")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test_user"));
    }

    @Test
    void shouldUpdateUsername() throws Exception {
        String payload = """
            {
              "username": "updated_user"
            }
        """;

        mockMvc.perform(put("/v1/users/me")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updated_user"));
    }

    @Test
    void shouldGetUserByUsername() throws Exception {
        mockMvc.perform(get("/v1/users/test_user")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@email.com"));
    }

    @Test
    void shouldDeleteOwnAccount() throws Exception {
        mockMvc.perform(delete("/v1/users/me")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectUnauthenticatedRequests() throws Exception {
        mockMvc.perform(get("/v1/users/me"))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"fail\"}"))
                .andExpect(status().isForbidden());
    }
}
