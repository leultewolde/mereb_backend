package app.mereb.mereb_backend.unit.validators;

import app.mereb.mereb_backend.like.Like;
import jakarta.validation.*;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LikeValidationTest {

    private Validator getValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator();
        }
    }

    @Test
    void validLike_passesValidation() {
        Validator validator = getValidator();
        Like like = new Like(UUID.randomUUID(), UUID.randomUUID());
        Set<ConstraintViolation<Like>> violations = validator.validate(like);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidLike_missingIds_failsValidation() {
        Validator validator = getValidator();
        Like like = new Like(null, null);
        Set<ConstraintViolation<Like>> violations = validator.validate(like);
        assertEquals(2, violations.size());
    }
}

