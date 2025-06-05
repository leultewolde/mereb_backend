package app.mereb.mereb_backend.contact;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContactRepository extends JpaRepository<Contact, UUID> {
    List<Contact> findByPhoneNumberIn(List<String> phoneNumbers);
}
