package user_service.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import user_service.service.constants.AppConstants;

@Data
public class UpdateProfileRequest {
    @Size(min = AppConstants.MIN_USERNAME_LENGTH, max = AppConstants.MAX_USERNAME_LENGTH)
    private String username;

    @Email
    @Size(max = AppConstants.MAX_EMAIL_LENGTH)
    private String email;

    @Size(min = AppConstants.MIN_PASSWORD_LENGTH, max = AppConstants.MAX_PASSWORD_LENGTH)
    private String password; // optional, if present will be updated

    @Size(max = AppConstants.MAX_GENRE_PREFERENCE_LENGTH)
    private String genrePreference; // optional

    @Size(max = AppConstants.MAX_LANGUAGE_PREFERENCE_LENGTH)
    private String languagePreference; // optional
}
