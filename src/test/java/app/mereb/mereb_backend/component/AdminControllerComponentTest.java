package app.mereb.mereb_backend.component;

import app.mereb.mereb_backend.auth.JwtUtil;
import app.mereb.mereb_backend.post.Post;
import app.mereb.mereb_backend.post.PostRepository;
import app.mereb.mereb_backend.post.PostType;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class AdminControllerComponentTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private PostRepository postRepository;

    private String adminToken;
    private UUID userId;
    private UUID postId;

    @BeforeEach
    void setup() {
        postRepository.deleteAll();
        userRepository.deleteAll();

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("test_user")
                .role(Role.USER)
                .email("user@email.com")
                .phone("123456789")
                .passwordHash("userPassword")
                .build();

        User admin = User.builder()
                .id(UUID.randomUUID())
                .username("admin_user")
                .role(Role.ADMIN)
                .email("admin@email.com")
                .phone("999999999")
                .passwordHash("adminPassword")
                .build();

        userRepository.saveAll(List.of(user, admin));
        adminToken = jwtUtil.generateToken(admin);
        userId = user.getId();

        Post post = Post.builder()
                .id(UUID.randomUUID())
                .userId(user.getId())
                .content("Post to manage")
                .type(PostType.TEXT)
                .isHidden(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        postRepository.save(post);
        postId = post.getId();
    }

    @Test
    void adminCanGetAllUsers() throws Exception {
        mockMvc.perform(get("/v1/admin/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void adminCanSuspendUser() throws Exception {
        mockMvc.perform(patch("/v1/admin/users/" + userId + "/suspend?suspend=true")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.suspended").value(true));
    }

    @Test
    void adminCanDeleteUser() throws Exception {
        mockMvc.perform(delete("/v1/admin/users/" + userId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void adminCanGetAllPosts() throws Exception {
        mockMvc.perform(get("/v1/admin/posts")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void adminCanTogglePostVisibility() throws Exception {
        String payload = "{\"hidden\":true}";

        mockMvc.perform(patch("/v1/admin/posts/" + postId + "/visibility")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());
    }

    @Test
    void adminCanDeletePost() throws Exception {
        mockMvc.perform(delete("/v1/admin/posts/" + postId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}

