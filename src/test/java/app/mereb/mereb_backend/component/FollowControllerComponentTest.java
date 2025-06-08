package app.mereb.mereb_backend.component;

import app.mereb.mereb_backend.auth.JwtUtil;
import app.mereb.mereb_backend.follow.FollowRepository;
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

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class FollowControllerComponentTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private FollowRepository followRepository;

    private String userToken;
    private String targetUsername;

    @BeforeEach
    void setup() {
        followRepository.deleteAll();
        userRepository.deleteAll();

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("follower")
                .role(Role.USER)
                .email("follower@email.com")
                .phone("1234567890")
                .passwordHash("password")
                .build();

        User target = User.builder()
                .id(UUID.randomUUID())
                .username("followed")
                .role(Role.USER)
                .email("followed@email.com")
                .phone("9999999999")
                .passwordHash("password")
                .build();

        userRepository.saveAll(List.of(user, target));
        userToken = jwtUtil.generateToken(user);
        targetUsername = target.getUsername();
    }

    @Test
    void shouldFollowUser() throws Exception {
        mockMvc.perform(post("/v1/follow/" + targetUsername)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUnfollowUser() throws Exception {
        mockMvc.perform(post("/v1/follow/" + targetUsername)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/v1/follow/" + targetUsername)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnTrueIfFollowing() throws Exception {
        mockMvc.perform(post("/v1/follow/" + targetUsername)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/v1/follow/is-following/" + targetUsername)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void shouldReturnCorrectFollowerAndFollowingCount() throws Exception {
        mockMvc.perform(post("/v1/follow/" + targetUsername)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/v1/follow/" + targetUsername + "/followers/count")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        mockMvc.perform(get("/v1/follow/follower/following/count")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void shouldRejectUnauthenticatedFollowRequests() throws Exception {
        mockMvc.perform(post("/v1/follow/" + targetUsername))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/v1/follow/" + targetUsername))
                .andExpect(status().isForbidden());
    }
}
