package nexi.cinetix.theatre_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/theatres/**", "/showtimes/**", "/actuator/**").permitAll()
                        .anyRequest().permitAll()
                )
                .httpBasic(b -> {})
                .formLogin(f -> f.disable())
                .logout(l -> l.disable());
        return http.build();
    }
}
