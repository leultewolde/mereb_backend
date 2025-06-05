package app.mereb.mereb_backend.contact;

import app.mereb.mereb_backend.user.User;
import app.mereb.mereb_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/contacts")
@RequiredArgsConstructor
public class ContactController {
    private final ContactRepository contactRepo;
    private final UserRepository userRepo;

    @PostMapping("/upload")
    public ResponseEntity<List<User>> uploadContacts(Authentication auth, @RequestBody List<String> phoneNumbers) {
        User user = (User) auth.getPrincipal();
        for (String phone : phoneNumbers) {
            Contact c = new Contact();
            c.setId(UUID.randomUUID());
            c.setUserId(user.getId());
            c.setPhoneNumber(phone);
            contactRepo.save(c);
        }
        List<User> matched = userRepo.findByPhoneIn(phoneNumbers);
        return ResponseEntity.ok(matched);
    }
}
