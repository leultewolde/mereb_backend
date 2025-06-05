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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class PostControllerComponentTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setup() {
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
                .username("test_admin")
                .role(Role.ADMIN)
                .email("admin@mereb.app")
                .phone("523456789")
                .passwordHash("adminPassword")
                .build();

        userRepository.saveAll(List.of(user, admin));

        userToken = jwtUtil.generateToken(user);
        adminToken = jwtUtil.generateToken(admin);

        Post post1 = Post.builder()
                .id(UUID.randomUUID())
                .userId(user.getId())
                .content("First post")
                .type(PostType.TEXT)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .isHidden(false)
                .build();
        Post post2 = Post.builder()
                .id(UUID.randomUUID())
                .userId(user.getId())
                .content("Second post")
                .type(PostType.TEXT)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .isHidden(false)
                .build();

        postRepository.saveAll(List.of(post1, post2));
    }

    @Test
    void userCanCreatePost() throws Exception {
        String payload = "{\"content\":\"Hello World\"}";

        mockMvc.perform(post("/v1/posts")
                        .content(payload)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void adminCanCreatePost() throws Exception {
        String payload = "{\"content\":\"Hello World\"}";

        mockMvc.perform(post("/v1/posts")
                        .content(payload)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldNotCreatePostWithBlankContent() throws Exception {
        String payload = """
                {
                  "content": ""
                }
                """;
        mockMvc.perform(post("/v1/posts")
                        .content(payload)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.content").value("Content must not be blank"));
    }

    @Test
    void shouldNotCreatePostWhenContentExceeds300Chars() throws Exception {
        String longContent = "a".repeat(301);
        String invalidPostJson = String.format("{\"content\":\"%s\"}", longContent);

        mockMvc.perform(post("/v1/posts")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPostJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.content").value("Content must not exceed 300 characters"));

    }
    @Test
    void unauthenticatedCannotCreatePost() throws Exception {
        mockMvc.perform(post("/v1/posts")
                        .content("{\"content\":\"test\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnFeedForAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/v1/posts/feed")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("Second post"));
    }

    @Test
    void shouldReturnPostsByUsernameForAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/v1/posts/users/test_user", userToken)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("Second post"));
    }

    @Test
    void shouldRejectRequestWithoutToken() throws Exception {
        mockMvc.perform(get("/v1/posts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectRequestWithInvalidToken() throws Exception {
        mockMvc.perform(get("/v1/posts")
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
    }
}
