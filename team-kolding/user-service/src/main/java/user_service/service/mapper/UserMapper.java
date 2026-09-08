package user_service.service.mapper;

import user_service.service.dto.RegisterRequest;
import user_service.service.dto.RegisterResponse;
import user_service.service.entity.User;
import user_service.service.constants.AppConstants;

/**
 * Utility mapper for converting between registration DTOs and the User entity.
 * Responsibility:
 *  - toEntity: Build a new User from an incoming RegisterRequest (raw password; encode in service layer).
 *  - toDto: Build a RegisterResponse from a persisted User (with a success message).
 */
public final class UserMapper {

    private UserMapper(){}
    /**
     * Map RegisterRequest -> User entity.
     * Notes:
     *  - Password is copied as-is; encode (hash) it before saving in the service.
     *  - Role defaults to AppConstants.DEFAULT_USER_ROLE if request role is null/blank.
     *  - Preferences copied directly (validated via annotations on DTO).
     *  - createdAt is managed by @CreationTimestamp; not set here.
     * @param request incoming registration data (validated)
     * @return new User entity or null if request is null
     */
    public static User toEntity(RegisterRequest request) {
        if (request == null) {
            return null;
        }
        User user = new User();
        user.setUsername(safeTrim(request.getUsername()));
        user.setEmail(safeTrim(request.getEmail()));
        user.setPassword(request.getPassword()); // encode later

        // Role (optional) -> default if missing
        String role = safeTrim(request.getRole());
        if (role == null || role.isEmpty()) {
            user.setRole(AppConstants.DEFAULT_USER_ROLE);
        } else {
            // Normalize role (e.g., ensure uppercase for consistency)
            user.setRole(role.toUpperCase());
        }

        // Preferences (validated @NotBlank so should be non-null)
        user.setGenrePreference(safeTrim(request.getGenrePreference()));
        user.setLanguagePreference(safeTrim(request.getLanguagePreference()));

        return user;
    }

    /**
     * Map User entity -> RegisterResponse DTO.
     * @param user persisted user entity
     * @return response DTO or null if user is null
     */
    public static RegisterResponse toDto(User user) {
        if (user == null) {
            return null;
        }
        return new RegisterResponse(
                AppConstants.REGISTRATION_SUCCESS_MESSAGE,
                user.getUsername(),
                user.getEmail()
        );
    }

    // Helper to trim and return null if input is null
    private static String safeTrim(String value) {
        return value == null ? null : value.trim();
    }
}
