package nexi.cinetix.movie_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource(properties = {
    "app.cors.enabled=true",
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class CorsConfigurationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    void corsConfig_canBeInstantiated() {
        CorsConfig config = new CorsConfig();
        assertNotNull(config);
    }

    @Test
    void corsConfig_isConfigurationClass() {
        assertTrue(CorsConfig.class.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
    }

    @Test
    void corsConfig_hasConditionalOnPropertyAnnotation() {
        assertTrue(CorsConfig.class.isAnnotationPresent(org.springframework.boot.autoconfigure.condition.ConditionalOnProperty.class));

        org.springframework.boot.autoconfigure.condition.ConditionalOnProperty annotation =
            CorsConfig.class.getAnnotation(org.springframework.boot.autoconfigure.condition.ConditionalOnProperty.class);

        assertNotNull(annotation);
        assertEquals("app.cors.enabled", annotation.name()[0]);
        assertEquals("true", annotation.havingValue());
    }

    @Test
    void corsConfig_corsConfigurerBeanCreated() {
        CorsConfig config = new CorsConfig();
        WebMvcConfigurer configurer = config.corsConfigurer();
        assertNotNull(configurer);
    }

    @Test
    void corsConfig_preflightOptionsRequest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(options("/api/movies")
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().exists("Access-Control-Allow-Methods"))
                .andExpect(header().exists("Access-Control-Allow-Headers"))
                .andExpect(header().exists("Access-Control-Allow-Credentials"))
                .andExpect(header().exists("Access-Control-Max-Age"));
    }

    @Test
    void corsConfig_getRequestWithOrigin() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(get("/api/movies")
                .header("Origin", "http://localhost:4200"))
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }

    @Test
    void corsConfig_postRequestWithOrigin() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(post("/api/movies")
                .header("Origin", "http://localhost:4200")
                .header("Content-Type", "application/json")
                .content("{}"))
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }

    @Test
    void corsConfig_putRequestWithOrigin() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(put("/api/movies/1")
                .header("Origin", "http://localhost:4200")
                .header("Content-Type", "application/json")
                .content("{}"))
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }

    @Test
    void corsConfig_deleteRequestWithOrigin() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(delete("/api/movies/1")
                .header("Origin", "http://localhost:4200"))
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }

    @Test
    void corsConfig_allowedOriginConfiguration() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Test allowed origin
        mockMvc.perform(get("/api/movies")
                .header("Origin", "http://localhost:4200"))
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }

    @Test
    void corsConfig_allowCredentialsConfiguration() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(get("/api/movies")
                .header("Origin", "http://localhost:4200"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    void corsConfig_maxAgeConfiguration() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(options("/api/movies")
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(header().string("Access-Control-Max-Age", "3600"));
    }

    @Test
    void corsConfig_allowedMethodsConfiguration() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(options("/api/movies")
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "POST"))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS"));
    }

    @Test
    void corsConfig_allowedHeadersConfiguration() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(options("/api/movies")
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", "Content-Type,Authorization"))
                .andExpect(header().string("Access-Control-Allow-Headers", "Content-Type, Authorization"));
    }

    @Test
    void corsConfig_exposedHeadersConfiguration() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(options("/api/movies")
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "POST"))
                .andExpect(header().string("Access-Control-Expose-Headers", "Location"));
    }
}
