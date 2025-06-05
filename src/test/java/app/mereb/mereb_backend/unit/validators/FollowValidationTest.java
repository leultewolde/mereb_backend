package app.mereb.mereb_backend.unit.validators;

import app.mereb.mereb_backend.follow.Follow;
import jakarta.validation.*;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class FollowValidationTest {

    private Validator getValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator();
        }
    }

    @Test
    void validFollow_passesValidation() {
        Validator validator = getValidator();
        Follow follow = new Follow(UUID.randomUUID(), UUID.randomUUID());
        Set<ConstraintViolation<Follow>> violations = validator.validate(follow);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidFollow_missingIds_failsValidation() {
        Validator validator = getValidator();
        Follow follow = new Follow(null, null);
        Set<ConstraintViolation<Follow>> violations = validator.validate(follow);
        assertEquals(2, violations.size());
    }
}

