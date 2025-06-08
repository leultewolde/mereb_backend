package app.mereb.mereb_backend.component;

import app.mereb.mereb_backend.auth.JwtUtil;
import app.mereb.mereb_backend.contact.ContactRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class ContactControllerComponentTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private ContactRepository contactRepository;

    private String userToken;

    @BeforeEach
    void setup() {
        contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("test_user")
                .role(Role.USER)
                .email("test@email.com")
                .phone("1111111111")
                .passwordHash("password")
                .build();

        User match = User.builder()
                .id(UUID.randomUUID())
                .username("match_user")
                .role(Role.USER)
                .email("match@email.com")
                .phone("9999999999")
                .passwordHash("password")
                .build();

        userRepository.saveAll(List.of(user, match));
        userToken = jwtUtil.generateToken(user);
    }

    @Test
    void shouldUploadContactsAndReturnMatchedUsers() throws Exception {
        String payload = "[\"9999999999\", \"0000000000\"]";

        mockMvc.perform(post("/v1/contacts/upload")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("match_user"));
    }

    @Test
    void shouldRejectIfUnauthenticated() throws Exception {
        String payload = "[\"1234567890\"]";

        mockMvc.perform(post("/v1/contacts/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldHandleEmptyList() throws Exception {
        String payload = "[]";

        mockMvc.perform(post("/v1/contacts/upload")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
