package user_service.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import user_service.service.constants.AppConstants;
import user_service.service.dto.*;
import user_service.service.entity.User;
import user_service.service.repository.UserRepository;
import user_service.service.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    private UserController controller;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
        objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        controller = new UserController(userService, userRepository);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setMessageConverters(
                        new StringHttpMessageConverter(StandardCharsets.UTF_8),
                        new MappingJackson2HttpMessageConverter(objectMapper)
                )
                .setValidator(validator())
                .build();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    private LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean v = new LocalValidatorFactoryBean();
        v.afterPropertiesSet();
        return v;
    }

    private void authenticate(String username, String... roles) {
        var authorities = java.util.Arrays.stream(roles)
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .map(SimpleGrantedAuthority::new)
                .toList();
        var auth = new UsernamePasswordAuthenticationToken(username, "pw", authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void register_success() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("testuser");
        req.setPassword("password123");
        req.setEmail("test@example.com");
        req.setGenrePreference("Action,Comedy");
        req.setLanguagePreference("English,Spanish");

        when(userService.register(any(RegisterRequest.class)))
                .thenReturn(new RegisterResponse("Registration successful","testuser","test@example.com"));

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registration successful"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void login_success() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("testuser");
        req.setPassword("pass");
        when(userService.login(any(LoginRequest.class))).thenReturn(new AuthResponse("jwt-token"));

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void getProfileById_unauthorized() throws Exception {
        mockMvc.perform(get("/user/profile/7"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(AppConstants.MSG_UNAUTHORIZED));
    }

    @Test
    void getProfileById_forbidden() throws Exception {
        authenticate("alice","USER");
        when(userService.getProfileForRequester("alice",9L))
                .thenThrow(new AccessDeniedException("denied"));
        mockMvc.perform(get("/user/profile/9"))
                .andExpect(status().isForbidden())
                .andExpect(content().string(AppConstants.MSG_FORBIDDEN));
    }

    @Test
    void getProfileById_success() throws Exception {
        authenticate("testuser","USER");
        ProfileResponse profile = new ProfileResponse(
                1L,"testuser","test@example.com","USER",
                "Action,Comedy","English,Spanish", Instant.now());
        when(userService.getProfileForRequester("testuser",1L)).thenReturn(profile);

        mockMvc.perform(get("/user/profile/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void updateProfile_success() throws Exception {
        authenticate("testuser","USER");
        UpdateProfileRequest req = new UpdateProfileRequest();
        ProfileResponse updated = new ProfileResponse(
                1L,"testuser","new.email@example.com","USER",
                "Action,Thriller","English,French", Instant.now());
        when(userService.updateProfile(eq("testuser"), any(UpdateProfileRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/user/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new.email@example.com"));
    }

    @Test
    void getAllUsers_success() throws Exception {
        authenticate("admin","ADMIN");
        ProfileResponse p1 = new ProfileResponse(1L,"u1","u1@example.com","USER","Action","English",Instant.now());
        ProfileResponse p2 = new ProfileResponse(2L,"u2","u2@example.com","USER","Comedy","Spanish",Instant.now());
        when(userService.getAllUsers()).thenReturn(List.of(p1,p2));

        mockMvc.perform(get("/user/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("u1"))
                .andExpect(jsonPath("$[1].username").value("u2"));
    }

    @Test
    void getOwnProfile_unauthorized() throws Exception {
        mockMvc.perform(get("/user/profile/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(AppConstants.MSG_UNAUTHORIZED));
    }

    @Test
    void getOwnProfile_success() throws Exception {
        authenticate("selfuser","USER");
        ProfileResponse profile = new ProfileResponse(
                5L,"selfuser","self@example.com","USER","Action","English", Instant.now());
        when(userService.getProfileByUsername("selfuser")).thenReturn(profile);

        mockMvc.perform(get("/user/profile/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.username").value("selfuser"));
    }

    @Test
    void getAllUserIdsInternal_success() throws Exception {
        authenticate("internal","USER");
        User u1 = new User(); u1.setId(10L);
        User u2 = new User(); u2.setId(20L);
        when(userRepository.findAll()).thenReturn(List.of(u1,u2));

        mockMvc.perform(get("/user/internal/ids"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(10L))
                .andExpect(jsonPath("$[1]").value(20L));
    }
}
