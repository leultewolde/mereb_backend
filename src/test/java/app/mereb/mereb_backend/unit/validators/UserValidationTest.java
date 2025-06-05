package app.mereb.mereb_backend.unit.validators;

import app.mereb.mereb_backend.user.Role;
import app.mereb.mereb_backend.user.User;
import jakarta.validation.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidationTest {

    private Validator getValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator();
        }
    }

    @Test
    void validUser_passesValidation() {
        Validator validator = getValidator();
        User user = new User(
                UUID.randomUUID(),
                "user@example.com",
                "1234567890",
                "testuser",
                "securepassword",
                Role.USER
        );
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidUser_missingFields_failsValidation() {
        Validator validator = getValidator();
        User user = new User();
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Should fail for: email, phone, username, passwordHash, role
        assertEquals(4, violations.size());
    }

    @Test
    void invalidUser_emailFormat_fails() {
        Validator validator = getValidator();
        User user = new User(
                UUID.randomUUID(),
                "invalid-email",
                "1234567890",
                "testuser",
                "securepassword",
                Role.USER
        );
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void invalidUser_shortPassword_fails() {
        Validator validator = getValidator();
        User user = new User(
                UUID.randomUUID(),
                "user@example.com",
                "1234567890",
                "testuser",
                "short",
                Role.USER
        );
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("passwordHash")));
    }
}
