package user_service.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import user_service.service.constants.AppConstants;

@Data
public class RegisterRequest {
    @NotBlank
    @Size(min = AppConstants.MIN_USERNAME_LENGTH, max = AppConstants.MAX_USERNAME_LENGTH)
    private String username;

    @NotBlank
    @Email
    @Size(max = AppConstants.MAX_EMAIL_LENGTH)
    private String email;

    @NotBlank
    @Size(min = AppConstants.MIN_PASSWORD_LENGTH, max = AppConstants.MAX_PASSWORD_LENGTH)
    private String password;

    private String role; // optional; defaults to USER if null

    @NotBlank
    @Size(max = AppConstants.MAX_GENRE_PREFERENCE_LENGTH)
    private String genrePreference;

    @NotBlank
    @Size(max = AppConstants.MAX_LANGUAGE_PREFERENCE_LENGTH)
    private String languagePreference;
}
