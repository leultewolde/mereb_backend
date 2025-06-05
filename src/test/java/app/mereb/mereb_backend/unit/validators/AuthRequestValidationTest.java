package app.mereb.mereb_backend.unit.validators;

import app.mereb.mereb_backend.auth.LoginRequest;
import app.mereb.mereb_backend.auth.RegisterRequest;
import jakarta.validation.*;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AuthRequestValidationTest {

    private Validator getValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator();
        }
    }

    @Test
    void validRegisterRequest_passes() {
        RegisterRequest request = new RegisterRequest(
                "test@example.com", "1234567890", "testuser", "strongpassword"
        );
        Set<ConstraintViolation<RegisterRequest>> violations = getValidator().validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidRegisterRequest_missingFields_fails() {
        RegisterRequest request = new RegisterRequest();
        Set<ConstraintViolation<RegisterRequest>> violations = getValidator().validate(request);
        assertEquals(4, violations.size());
    }

    @Test
    void validLogin_withUsername_passes() {
        LoginRequest request = new LoginRequest(null, null, "testuser", "password123");
        Set<ConstraintViolation<LoginRequest>> violations = getValidator().validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validLogin_withEmail_passes() {
        LoginRequest request = new LoginRequest("test@example.com", null, null, "password123");
        Set<ConstraintViolation<LoginRequest>> violations = getValidator().validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidLogin_missingAllIdentifiers_failsManually() {
        LoginRequest request = new LoginRequest(null, null, null, "password123");
        Set<ConstraintViolation<LoginRequest>> violations = getValidator().validate(request);
        assertTrue(violations.isEmpty()); // No annotation violation
        // You must manually check this in your login logic
    }

    @Test
    void invalidLogin_missingPassword_fails() {
        LoginRequest request = new LoginRequest("test@example.com", null, null, "");
        Set<ConstraintViolation<LoginRequest>> violations = getValidator().validate(request);
        assertFalse(violations.isEmpty());
    }
}
