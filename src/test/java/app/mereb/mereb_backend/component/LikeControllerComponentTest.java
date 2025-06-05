package app.mereb.mereb_backend.component;

import app.mereb.mereb_backend.auth.JwtUtil;
import app.mereb.mereb_backend.like.LikeRepository;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class LikeControllerComponentTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private LikeRepository likeRepository;

    private String userToken;
    private UUID postId;

    @BeforeEach
    void setup() {
        likeRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("like_user")
                .role(Role.USER)
                .email("like@email.com")
                .phone("1234567890")
                .passwordHash("password")
                .build();

        userRepository.save(user);
        userToken = jwtUtil.generateToken(user);

        Post post = Post.builder()
                .id(UUID.randomUUID())
                .userId(user.getId())
                .content("Post to like")
                .type(PostType.TEXT)
                .isHidden(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        postRepository.save(post);
        postId = post.getId();
    }

    @Test
    void shouldLikePostSuccessfully() throws Exception {
        mockMvc.perform(post("/v1/likes/" + postId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectDuplicateLikes() throws Exception {
        mockMvc.perform(post("/v1/likes/" + postId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/v1/likes/" + postId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isConflict())
                .andExpect(content().string("Already liked"));
    }

    @Test
    void shouldUnlikePostSuccessfully() throws Exception {
        mockMvc.perform(post("/v1/likes/" + postId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/v1/likes/" + postId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn404IfUnlikingNonLikedPost() throws Exception {
        mockMvc.perform(delete("/v1/likes/" + postId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Like not found"));
    }

    @Test
    void shouldRejectUnauthenticatedLikeRequests() throws Exception {
        mockMvc.perform(post("/v1/likes/" + postId))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/v1/likes/" + postId))
                .andExpect(status().isUnauthorized());
    }
}

