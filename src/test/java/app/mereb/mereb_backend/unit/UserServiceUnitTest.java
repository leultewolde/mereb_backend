package app.mereb.mereb_backend.unit;

import app.mereb.mereb_backend.user.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUser() {
        User mockUser = new User();
        mockUser.setUsername("john");

        UserResponseDTO mockUserDTO = new UserResponseDTO();
        mockUserDTO.setUsername("john");

        when(userRepository.save(mockUser)).thenReturn(mockUser);
        when(userMapper.toDto(mockUser)).thenReturn(mockUserDTO);

        UserResponseDTO result = userService.createUser(mockUser);

        assertNotNull(result);
        assertEquals("john", result.getUsername());

        verify(userRepository).save(mockUser);
        verify(userMapper).toDto(mockUser);
    }

    @Test
    void shouldGetAllUsers() {
        User mockUser = new User();
        mockUser.setUsername("john");

        UserResponseDTO mockUserDTO = new UserResponseDTO();
        mockUserDTO.setUsername("john");

        when(userRepository.findAll()).thenReturn(List.of(mockUser));
        when(userMapper.toDtoList(List.of(mockUser)))
                .thenReturn(List.of(mockUserDTO));

        List<UserResponseDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("john", result.get(0).getUsername());

        verify(userRepository).findAll();
        verify(userMapper).toDtoList(List.of(mockUser));
    }

    @Test
    void shouldGetUserByUsername() {
        User mockUser = new User();
        mockUser.setUsername("john");

        UserResponseDTO mockUserDTO = new UserResponseDTO();
        mockUserDTO.setUsername("john");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(mockUser));
        when(userMapper.toDto(mockUser))
                .thenReturn(mockUserDTO);

        UserResponseDTO result = userService.getUserByUsername("john");

        assertNotNull(result);
        assertEquals("john", result.getUsername());

        verify(userRepository).findByUsername("john");
        verify(userMapper).toDto(mockUser);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundByUsername() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.getUserByUsername("unknown"));

        verify(userRepository).findByUsername("unknown");
    }


    @Test
    void shouldUpdateRole() {
        UUID uuid = UUID.randomUUID();

        User mockUser = new User();
        mockUser.setId(uuid);
        mockUser.setUsername("john");
        mockUser.setRole(Role.USER);
        mockUser.setUpdatedAt(Instant.now());

        User mockAdmin = new User();
        mockAdmin.setId(uuid);
        mockAdmin.setUsername("john");
        mockAdmin.setRole(Role.ADMIN);
        mockAdmin.setUpdatedAt(Instant.now());

        UserResponseDTO mockAdminDTO = new UserResponseDTO();
        mockAdminDTO.setId(uuid);
        mockAdminDTO.setUsername("john");
        mockAdminDTO.setRole(Role.ADMIN);
        mockAdminDTO.setUpdatedAt(Instant.now());

        when(userRepository.findById(uuid)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockAdmin);
        when(userMapper.toDto(any(User.class)))
                .thenReturn(mockAdminDTO);

        UserResponseDTO result = userService.updateRole(uuid, Role.ADMIN);

        assertNotNull(result);
        assertEquals("john", result.getUsername());
        assertEquals(uuid, result.getId());
        assertEquals(Role.ADMIN, result.getRole());

        verify(userRepository).findById(uuid);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingRoleAndUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.updateRole(id, Role.ADMIN));

        verify(userRepository).findById(id);
    }


    @Test
    void shouldUpdateSuspended() {
        UUID uuid = UUID.randomUUID();

        User mockUser = new User();
        mockUser.setId(uuid);
        mockUser.setUsername("john");
        mockUser.setSuspended(false);

        User mockSuspendedUser = new User();
        mockSuspendedUser.setId(uuid);
        mockSuspendedUser.setUsername("john");
        mockSuspendedUser.setSuspended(true);

        UserResponseDTO mockSuspendedDTO = new UserResponseDTO();
        mockSuspendedDTO.setId(uuid);
        mockSuspendedDTO.setUsername("john");
        mockSuspendedDTO.setSuspended(true);

        when(userRepository.findById(uuid)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockSuspendedUser);
        when(userMapper.toDto(any(User.class)))
                .thenReturn(mockSuspendedDTO);

        UserResponseDTO result = userService.updateSuspended(uuid, true);

        assertNotNull(result);
        assertEquals("john", result.getUsername());
        assertEquals(uuid, result.getId());
        assertTrue(result.isSuspended());

        verify(userRepository).findById(uuid);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingSuspendedAndUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.updateSuspended(id, true));

        verify(userRepository).findById(id);
    }


    @Test
    void shouldUpdateProfile() {
        UUID uuid = UUID.randomUUID();

        User mockUser = new User();
        mockUser.setId(uuid);
        mockUser.setUsername("john");
        mockUser.setPasswordHash("passwordHashed");

        User mockUpdatedUser = new User();
        mockUpdatedUser.setId(uuid);
        mockUpdatedUser.setUsername("john1");
        mockUpdatedUser.setPasswordHash("password123Hashed");

        UserResponseDTO mockUpdatedDTO = new UserResponseDTO();
        mockUpdatedDTO.setId(uuid);
        mockUpdatedDTO.setUsername("john1");

        when(encoder.encode("password123")).thenReturn("password123Hashed");
        when(userRepository.save(any(User.class))).thenReturn(mockUpdatedUser);
        when(userMapper.toDto(any(User.class)))
                .thenReturn(mockUpdatedDTO);

        Map<String, String> updates = new HashMap<>();
        updates.put("username", "john1");
        updates.put("password", "password123");

        UserResponseDTO result = userService.updateProfile(mockUser, updates);

        assertNotNull(result);
        assertEquals("john1", result.getUsername());

        verify(encoder).encode("password123");
        verify(userRepository).save(mockUser);
        verify(userMapper).toDto(mockUpdatedUser);
    }

    @Test
    void shouldDeleteUserById() {
        UUID uuid = UUID.randomUUID();

        doNothing().when(userRepository).deleteById(uuid);

        String result = userService.deleteUser(uuid);

        assertNotNull(result);
        assertEquals("Deleted", result);

        verify(userRepository).deleteById(uuid);
    }

    @Test
    void shouldDeleteUserByEntity() {
        User user = new User();

        doNothing().when(userRepository).delete(user);

        String result = userService.deleteUser(user);

        assertNotNull(result);
        assertEquals("Deleted", result);

        verify(userRepository).delete(user);
    }

    @Test
    void shouldGetAllAdmins() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setRole(Role.ADMIN);

        UserResponseDTO adminDTO = new UserResponseDTO();
        adminDTO.setUsername("admin");
        adminDTO.setRole(Role.ADMIN);

        when(userRepository.findByRoleIn(List.of(Role.ADMIN))).thenReturn(List.of(admin));
        when(userMapper.toDtoList(List.of(admin))).thenReturn(List.of(adminDTO));

        List<UserResponseDTO> result = userService.getAllAdmins();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).getUsername());
        assertEquals(Role.ADMIN, result.get(0).getRole());

        verify(userRepository).findByRoleIn(List.of(Role.ADMIN));
        verify(userMapper).toDtoList(List.of(admin));
    }

    @Test
    void shouldGetAllSuperAdmins() {
        User superAdmin = new User();
        superAdmin.setUsername("superadmin");
        superAdmin.setRole(Role.SUPER_ADMIN);

        UserResponseDTO dto = new UserResponseDTO();
        dto.setUsername("superadmin");
        dto.setRole(Role.SUPER_ADMIN);

        when(userRepository.findByRoleIn(List.of(Role.SUPER_ADMIN))).thenReturn(List.of(superAdmin));
        when(userMapper.toDtoList(List.of(superAdmin))).thenReturn(List.of(dto));

        List<UserResponseDTO> result = userService.getAllSuperAdmins();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("superadmin", result.get(0).getUsername());
        assertEquals(Role.SUPER_ADMIN, result.get(0).getRole());

        verify(userRepository).findByRoleIn(List.of(Role.SUPER_ADMIN));
        verify(userMapper).toDtoList(List.of(superAdmin));
    }


}
