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
class SuperAdminControllerComponentTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;

    private String superAdminToken;
    private UUID userId;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        User superAdmin = User.builder()
                .id(UUID.randomUUID())
                .username("superadmin")
                .role(Role.SUPER_ADMIN)
                .email("superadmin@email.com")
                .phone("9999999999")
                .passwordHash("secure123")
                .build();

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("regular_user")
                .role(Role.USER)
                .email("user@email.com")
                .phone("1111111111")
                .passwordHash("password")
                .build();

        User admin = User.builder()
                .id(UUID.randomUUID())
                .username("admin_user")
                .role(Role.ADMIN)
                .email("admin@email.com")
                .phone("2222222222")
                .passwordHash("adminpass")
                .build();

        userRepository.saveAll(List.of(superAdmin, user, admin));
        userId = user.getId();
        superAdminToken = jwtUtil.generateToken(superAdmin);
    }

    @Test
    void superAdminCanViewAllSuperAdmins() throws Exception {
        mockMvc.perform(get("/v1/superadmins")
                        .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("superadmin"));
    }

    @Test
    void superAdminCanViewAllAdmins() throws Exception {
        mockMvc.perform(get("/v1/superadmins/admins")
                        .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("admin_user"));
    }

    @Test
    void superAdminCanUpdateUserRole() throws Exception {
        mockMvc.perform(patch("/v1/superadmins/users/" + userId + "/role")
                        .header("Authorization", "Bearer " + superAdminToken)
                        .param("role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void nonSuperAdminCannotAccessEndpoints() throws Exception {
        User user = userRepository.findByUsername("regular_user").orElseThrow();
        String userToken = jwtUtil.generateToken(user);

        mockMvc.perform(get("/v1/superadmins")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/v1/superadmins/users/" + userId + "/role")
                        .header("Authorization", "Bearer " + userToken)
                        .param("role", "ADMIN"))
                .andExpect(status().isForbidden());
    }
}
