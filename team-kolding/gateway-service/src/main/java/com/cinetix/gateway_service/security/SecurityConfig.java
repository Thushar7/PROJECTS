package com.cinetix.gateway_service.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection since we're using JWTs (stateless authentication)
                .csrf(AbstractHttpConfigurer::disable)
                // Enable CORS with default configuration
                .cors(Customizer.withDefaults())
                // Define authorization rules for HTTP requests
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/user/register", "/user/login").permitAll() // added public auth endpoints
                        .requestMatchers("/movies/**","/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/theatres", "/theatres/**").permitAll()
                        .requestMatchers("/notifications", "/notifications/**").permitAll()
                        .requestMatchers("/showtimes", "/showtimes/**").permitAll()
                        .requestMatchers("/notifications", "/notifications/**").permitAll()
                        // Role-based examples (authorities populated from JWT 'role' claim):
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").permitAll()
                        .anyRequest().authenticated()
                )
                // Centralized JSON responses for auth failures (401) and access denials (403)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, authEx) -> {
                            if (!res.isCommitted()) {
                                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                res.setContentType("application/json");
                                res.getWriter().write("{\"message\":\"Unauthorized\"}");
                            }
                        })
                        .accessDeniedHandler((req, res, deniedEx) -> {
                            if (!res.isCommitted()) {
                                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                res.setContentType("application/json");
                                res.getWriter().write("{\"message\":\"Forbidden\"}");
                            }
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // Expose the AuthenticationManager bean to be used in authentication processes
        return authenticationConfiguration.getAuthenticationManager();
    }
}
