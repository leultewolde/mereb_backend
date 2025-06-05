package app.mereb.mereb_backend.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    List<User> findByPhoneIn(List<String> phones);

    List<User> findByRoleIn(List<Role> roles);
}
