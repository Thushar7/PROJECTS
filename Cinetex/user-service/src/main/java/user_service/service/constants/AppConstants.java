package user_service.service.constants;

/**
 * Application constants following company standards
 */
public final class AppConstants {

    // Private constructor to prevent instantiation
    private AppConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // User related constants
    public static final String DEFAULT_USER_ROLE = "USER";
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 50;
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 100;
    public static final int MAX_EMAIL_LENGTH = 100;
    public static final int MAX_ROLE_LENGTH = 30;
    public static final String REGISTRATION_SUCCESS_MESSAGE = "Registration successful";

    // Preference related constants
    public static final int MAX_GENRE_PREFERENCE_LENGTH = 50;
    public static final int MAX_LANGUAGE_PREFERENCE_LENGTH = 30;
    public static final String DEFAULT_GENRE_PREFERENCE = "GENERAL";
    public static final String DEFAULT_LANGUAGE_PREFERENCE = "English";

    // Error messages
    public static final String INVALID_CREDENTIALS_MESSAGE = "Invalid username or password";
    public static final String USER_NOT_FOUND_MESSAGE = "User not found";
    public static final String USERNAME_ALREADY_TAKEN_MESSAGE = "Username already taken";
    public static final String EMAIL_ALREADY_IN_USE_MESSAGE = "Email already in use";
    public static final String MSG_UNAUTHORIZED = "Unauthorized";
    public static final String MSG_FORBIDDEN = "Forbidden";
    // Database constraints
    public static final String UK_USERS_USERNAME = "uk_users_username";
    public static final String UK_USERS_EMAIL = "uk_users_email";
    public static final String USERS_TABLE_NAME = "users";

    // JWT Claims
    public static final String JWT_CLAIM_USER_ID = "uid";
    public static final String JWT_CLAIM_ROLE = "role";
}
