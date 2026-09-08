package nexi.cinetix.notification_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;

/**
 * DTO for user registration events consumed from user-service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String genrePreference;
    private String languagePreference;
    private Instant createdAt;
}
