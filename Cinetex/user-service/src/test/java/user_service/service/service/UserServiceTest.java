package user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.AccessDeniedException;
import user_service.service.constants.AppConstants;
import user_service.service.dto.*;
import user_service.service.entity.User;
import user_service.service.exception.InvalidCredentialsException;
import user_service.service.exception.UserNotFoundException;
import user_service.service.messaging.RabbitMQProducer;
import user_service.service.repository.UserRepository;
import user_service.service.security.utils.JwtUtil;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private RabbitMQProducer rabbitMQProducer; // FIX: missing dependency caused NPE

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encoded_password");
        testUser.setRole(AppConstants.DEFAULT_USER_ROLE);
        testUser.setGenrePreference("ACTION");
        testUser.setLanguagePreference("English");
    }

    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setGenrePreference("COMEDY");
        request.setLanguagePreference("Spanish");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(2L);
            return u;
        });

        RegisterResponse response = userService.register(request);

        assertNotNull(response);
        assertEquals("newuser", response.getUsername());
        assertEquals("new@example.com", response.getEmail());
        assertTrue(response.getMessage().contains("User registered successfully"));
        verify(rabbitMQProducer).publishUserRegistrationEvent(any(User.class));
    }

    @Test
    void register_duplicateUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("different@example.com");
        request.setPassword("password123");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> userService.register(request));
        assertEquals(AppConstants.USERNAME_ALREADY_TAKEN_MESSAGE, ex.getMessage());
        verify(userRepository, never()).save(any());
        verifyNoInteractions(rabbitMQProducer);
    }

    @Test
    void register_duplicateEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> userService.register(request));
        assertEquals(AppConstants.EMAIL_ALREADY_IN_USE_MESSAGE, ex.getMessage());
        verify(userRepository, never()).save(any());
        verifyNoInteractions(rabbitMQProducer);
    }

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);
        when(jwtUtil.generateToken("testuser", 1L, "USER")).thenReturn("test.jwt.token");

        AuthResponse response = userService.login(request);

        assertNotNull(response);
        assertEquals("test.jwt.token", response.getToken());
    }

    @Test
    void login_invalidCredentials_wrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrong");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong", "encoded_password")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.login(request));
    }

    @Test
    void login_invalidCredentials_userNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("ghost");
        request.setPassword("x");

        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> userService.login(request));
    }

    @Test
    void getProfileByUsername_found() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        ProfileResponse resp = userService.getProfileByUsername("testuser");

        assertEquals(1L, resp.getId());
        assertEquals("testuser", resp.getUsername());
    }

    @Test
    void getProfileByUsername_notFound() {
        when(userRepository.findByUsername("none")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getProfileByUsername("none"));
    }

    @Test
    void getProfileById_found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        ProfileResponse resp = userService.getProfileById(1L);
        assertEquals("testuser", resp.getUsername());
    }

    @Test
    void getProfileById_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getProfileById(99L));
    }

    @Test
    void getProfileForRequester_self() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ProfileResponse resp = userService.getProfileForRequester("testuser", 1L);
        assertEquals(1L, resp.getId());
    }

    @Test
    void getProfileForRequester_adminAccessOther() {
        User admin = new User();
        admin.setId(2L);
        admin.setUsername("admin");
        admin.setRole("ADMIN");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ProfileResponse resp = userService.getProfileForRequester("admin", 1L);
        assertEquals("testuser", resp.getUsername());
    }

    @Test
    void getProfileForRequester_forbidden() {
        User requester = new User();
        requester.setId(5L);
        requester.setUsername("userA");
        requester.setRole("USER");

        when(userRepository.findByUsername("userA")).thenReturn(Optional.of(requester));

        assertThrows(AccessDeniedException.class,
                () -> userService.getProfileForRequester("userA", 1L));
    }

    @Test
    void updateProfile_success() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setGenrePreference("THRILLER");
        req.setLanguagePreference("French");
        req.setPassword("newpass");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newpass")).thenReturn("enc_new");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ProfileResponse resp = userService.updateProfile("testuser", req);

        assertEquals("THRILLER", resp.getGenrePreference());
        assertEquals("French", resp.getLanguagePreference());
        verify(passwordEncoder).encode("newpass");
    }

    @Test
    void updateProfile_userNotFound() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.updateProfile("missing", req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getAllUsers() {
        User other = new User();
        other.setId(2L);
        other.setUsername("other");
        other.setEmail("o@example.com");
        other.setRole("USER");

        when(userRepository.findAll()).thenReturn(List.of(testUser, other));

        List<ProfileResponse> list = userService.getAllUsers();

        assertEquals(2, list.size());
        assertEquals("testuser", list.get(0).getUsername());
        assertEquals("other", list.get(1).getUsername());
    }
}
