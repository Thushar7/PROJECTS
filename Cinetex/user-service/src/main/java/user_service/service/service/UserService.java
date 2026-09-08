package user_service.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import user_service.service.constants.AppConstants;
import user_service.service.dto.AuthResponse;
import user_service.service.dto.LoginRequest;
import user_service.service.dto.ProfileResponse;
import user_service.service.dto.RegisterRequest;
import user_service.service.dto.RegisterResponse;
import user_service.service.dto.UpdateProfileRequest;
import user_service.service.exception.InvalidCredentialsException;
import user_service.service.exception.UserNotFoundException;
import user_service.service.entity.User;
import user_service.service.messaging.RabbitMQProducer;
import user_service.service.repository.UserRepository;
import user_service.service.security.utils.JwtUtil;
import user_service.service.mapper.UserMapper;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RabbitMQProducer rabbitMQProducer;

    // Added helper to centralize ProfileResponse creation including preferences
    private ProfileResponse toProfile(User user) {
        return new ProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getGenrePreference(),
                user.getLanguagePreference(),
                user.getCreatedAt()
        );
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.info("Attempting to register user with username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration failed: Username already taken - {}", request.getUsername());
            throw new IllegalArgumentException(AppConstants.USERNAME_ALREADY_TAKEN_MESSAGE);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email already in use - {}", request.getEmail());
            throw new IllegalArgumentException(AppConstants.EMAIL_ALREADY_IN_USE_MESSAGE);
        }

        // Use mapper to create entity from request
        User user = UserMapper.toEntity(request);
        // Encode password after mapping (mapper keeps raw password)
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Defensive fallback defaults (should be set by mapper / DTO validation)
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole(AppConstants.DEFAULT_USER_ROLE);
        }
        if (user.getGenrePreference() == null || user.getGenrePreference().isBlank()) {
            user.setGenrePreference(AppConstants.DEFAULT_GENRE_PREFERENCE);
        }
        if (user.getLanguagePreference() == null || user.getLanguagePreference().isBlank()) {
            user.setLanguagePreference(AppConstants.DEFAULT_LANGUAGE_PREFERENCE);
        }

        User saved = userRepository.save(user);
        log.info("User registered successfully with ID: {} and username: {}", saved.getId(), saved.getUsername());

        // Publish user registration event to RabbitMQ (sending User entity directly)
        rabbitMQProducer.publishUserRegistrationEvent(saved);

        // Use mapper for response then override message to preserve existing wording
        RegisterResponse response = UserMapper.toDto(saved);
        response.setMessage("User registered successfully. Please login to get your access token.");
        return response;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting login for username: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed: User not found - {}", request.getUsername());
                    return new InvalidCredentialsException(AppConstants.INVALID_CREDENTIALS_MESSAGE);
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed: Invalid password for username - {}", request.getUsername());
            throw new InvalidCredentialsException(AppConstants.INVALID_CREDENTIALS_MESSAGE);
        }

        log.info("User logged in successfully: {}", user.getUsername());
        String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole());
        return new AuthResponse(token);
    }


    @Transactional
    public ProfileResponse updateProfile(String currentUsername, UpdateProfileRequest request) {
        log.info("Attempting to update profile for username: {}", currentUsername);

        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> {
                    log.warn("Profile update failed: User not found - {}", currentUsername);
                    return new UserNotFoundException(AppConstants.USER_NOT_FOUND_MESSAGE);
                });

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            if (userRepository.existsByUsernameAndIdNot(request.getUsername(), user.getId())) {
                log.warn("Profile update failed: Username already taken - {}", request.getUsername());
                throw new IllegalArgumentException(AppConstants.USERNAME_ALREADY_TAKEN_MESSAGE);
            }
            user.setUsername(request.getUsername());
            log.debug("Username updated for user ID: {}", user.getId());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (userRepository.existsByEmailAndIdNot(request.getEmail(), user.getId())) {
                log.warn("Profile update failed: Email already in use - {}", request.getEmail());
                throw new IllegalArgumentException(AppConstants.EMAIL_ALREADY_IN_USE_MESSAGE);
            }
            user.setEmail(request.getEmail());
            log.debug("Email updated for user ID: {}", user.getId());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            log.debug("Password updated for user ID: {}", user.getId());
        }
        if (request.getGenrePreference() != null && !request.getGenrePreference().isBlank()) {
            user.setGenrePreference(request.getGenrePreference());
            log.debug("Genre preference updated for user ID: {}", user.getId());
        }
        if (request.getLanguagePreference() != null && !request.getLanguagePreference().isBlank()) {
            user.setLanguagePreference(request.getLanguagePreference());
            log.debug("Language preference updated for user ID: {}", user.getId());
        }
        User saved = userRepository.save(user);
        log.info("Profile updated successfully for user ID: {}", saved.getId());

        return toProfile(saved);
    }

    @Transactional(readOnly = true)
    public List<ProfileResponse> getAllUsers() {
        log.info("Admin requesting all users list");

        List<User> users = userRepository.findAll();
        log.info("Retrieved {} users from database", users.size());

        List<ProfileResponse> userProfiles = users.stream()
                .map(this::toProfile)
                .toList();

        log.debug("Converted {} users to ProfileResponse objects", userProfiles.size());
        return userProfiles;
    }

    // New method: retrieve profile by user id
    public ProfileResponse getProfileById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Profile retrieval failed: User id not found - {}", id);
                    return new UserNotFoundException(AppConstants.USER_NOT_FOUND_MESSAGE);
                });
        return toProfile(user);
    }

    // Added: authorize requester (self or ADMIN) before returning target profile
    public ProfileResponse getProfileForRequester(String requesterUsername, Long targetUserId) {
        User requester = userRepository.findByUsername(requesterUsername)
                .orElseThrow(() -> new UserNotFoundException(AppConstants.USER_NOT_FOUND_MESSAGE));
        if (!requester.getId().equals(targetUserId) && !"ADMIN".equalsIgnoreCase(requester.getRole())) {
            throw new org.springframework.security.access.AccessDeniedException("Forbidden");
        }
        return getProfileById(targetUserId);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(AppConstants.USER_NOT_FOUND_MESSAGE));
        return toProfile(user);
    }
}
