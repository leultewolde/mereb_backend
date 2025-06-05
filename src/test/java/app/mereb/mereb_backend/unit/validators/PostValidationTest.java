package app.mereb.mereb_backend.unit.validators;

import app.mereb.mereb_backend.post.Post;
import app.mereb.mereb_backend.post.PostType;
import jakarta.validation.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PostValidationTest {

    private Validator getValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator();
        }
    }

    @Test
    void validPost_passesValidation() {
        Validator validator = getValidator();
        Post post = new Post(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "This is a valid post content.",
                null,
                PostType.TEXT,
                false,
                Instant.now(),
                Instant.now()
        );
        Set<ConstraintViolation<Post>> violations = validator.validate(post);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidPost_missingFields_failsValidation() {
        Validator validator = getValidator();
        Post post = new Post(); // all fields null or defaults
        Set<ConstraintViolation<Post>> violations = validator.validate(post);

        assertEquals(1, violations.size());
    }

    @Test
    void invalidPost_longContent_fails() {
        Validator validator = getValidator();
        String longContent = "x".repeat(301); // exceeds max
        Post post = new Post(
                UUID.randomUUID(),
                UUID.randomUUID(),
                longContent,
                null,
                PostType.TEXT,
                false,
                Instant.now(),
                Instant.now()
        );
        Set<ConstraintViolation<Post>> violations = validator.validate(post);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("content")));
    }
}

