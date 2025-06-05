package app.mereb.mereb_backend.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;

    public UserResponseDTO createUser(User user) {
        return userMapper.toDto(userRepository.save(user));
    }

    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toDtoList(users);
    }

    public List<UserResponseDTO> getAllAdmins() {
        List<User> users = userRepository.findByRoleIn(List.of(Role.ADMIN));
        return userMapper.toDtoList(users);
    }

    public List<UserResponseDTO> getAllSuperAdmins() {
        List<User> users = userRepository.findByRoleIn(List.of(Role.SUPER_ADMIN));
        return userMapper.toDtoList(users);
    }

    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return userMapper.toDto(user);
    }

    public UserResponseDTO updateRole(UUID id, Role role) {
        User user = userRepository.findById(id).orElseThrow();
        Map<String, String> map = new HashMap<>();
        map.put("role", role.name());
        return this.updateUser(user, map);
    }

    public UserResponseDTO updateSuspended(UUID id, boolean isSuspended) {
        User user = userRepository.findById(id).orElseThrow();
        Map<String, String> map = new HashMap<>();
        map.put("isSuspended", String.valueOf(isSuspended));
        return this.updateUser(user, map);
    }

    public UserResponseDTO updateProfile(User user, Map<String, String> updates) {
        if (updates.containsKey("username")) user.setUsername(updates.get("username"));
        if (updates.containsKey("password")) user.setPasswordHash(encoder.encode(updates.get("password")));
        return userMapper.toDto(userRepository.save(user));
    }

    private UserResponseDTO updateUser(User user, Map<String, String> updates) {
        if (updates.containsKey("role")) user.setRole(getRole(updates.get("role")));
        if (updates.containsKey("isSuspended")) user.setSuspended(getSuspended(updates.get("isSuspended")));

        User savedUser = userRepository.save(user);

        log.info("User state before mapping - ID: {}, isSuspended: {}", savedUser.getId(), savedUser.isSuspended()); // CRITICAL CHECK
        UserResponseDTO dto = userMapper.toDto(savedUser);
        log.info("DTO state after mapping - ID: {}, isSuspended: {}", dto.getId(), dto.isSuspended()); // This is where you see false
        return dto;
    }

    public String deleteUser(UUID id) {
        userRepository.deleteById(id);
        return "Deleted";
    }

    public String deleteUser(User user) {
        userRepository.delete(user);
        return "Deleted";
    }

    private Role getRole(String roleStr) {
        try {
            return Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + roleStr);
        }
    }

    private boolean getSuspended(Object suspendedStr) {
        return Boolean.parseBoolean(suspendedStr.toString());
    }
}
