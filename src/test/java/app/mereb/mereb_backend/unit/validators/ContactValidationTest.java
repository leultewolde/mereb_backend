package app.mereb.mereb_backend.unit.validators;

import app.mereb.mereb_backend.contact.Contact;
import jakarta.validation.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ContactValidationTest {

    private Validator getValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator();
        }
    }

    @Test
    void validContact_passesValidation() {
        Validator validator = getValidator();
        Contact contact = new Contact(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "1234567890",
                Instant.now()
        );
        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidContact_missingFields_failsValidation() {
        Validator validator = getValidator();
        Contact contact = new Contact(null, null, null, null);
        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
        assertEquals(4, violations.size());
    }

    @Test
    void invalidContact_phoneTooLong_failsValidation() {
        Validator validator = getValidator();
        Contact contact = new Contact(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "1".repeat(21),
                Instant.now()
        );
        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phoneNumber")));
    }
}

