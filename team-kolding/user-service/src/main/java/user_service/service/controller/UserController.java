package user_service.service.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import user_service.service.constants.AppConstants;
import user_service.service.dto.*;
import user_service.service.entity.User;
import user_service.service.service.UserService;
import user_service.service.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/user") // Added base path to align with gateway predicate Path=/user/**
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository; // new dependency for internal endpoint

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping(path = "/profile/{id}")
    public ResponseEntity<Object> getProfileById(@PathVariable("id") Long id) {
        String username = getCurrentUsername();
        if (username == null || username.equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AppConstants.MSG_UNAUTHORIZED);
        }
        try {
            return ResponseEntity.ok(userService.getProfileForRequester(username, id)); // enforce auth
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(AppConstants.MSG_FORBIDDEN);
        }
    }

    @PutMapping(path = "/profile")
    public ResponseEntity<ProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        String username = getCurrentUsername();
        return ResponseEntity.ok(userService.updateProfile(username, request));
    }

    @GetMapping(path = "/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProfileResponse>> getAllUsers() {
        List<ProfileResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping(path = "/profile/me")
    public ResponseEntity<Object> getOwnProfile() {
        String username = getCurrentUsername();
        if (username == null || username.equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AppConstants.MSG_UNAUTHORIZED);
        }
        return ResponseEntity.ok(userService.getProfileByUsername(username));
    }

    @GetMapping(path = "/internal/ids")
    public ResponseEntity<java.util.List<Long>> getAllUserIdsInternal() {
        java.util.List<Long> ids = userRepository.findAll().stream().map(User::getId).toList();
        return ResponseEntity.ok(ids);
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
}
