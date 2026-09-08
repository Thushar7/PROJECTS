package user_service.service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import user_service.service.constants.AppConstants;

import java.time.Instant;

@Entity
@Table(name = AppConstants.USERS_TABLE_NAME, uniqueConstraints = {
        @UniqueConstraint(name = AppConstants.UK_USERS_USERNAME, columnNames = {"username"}),
        @UniqueConstraint(name = AppConstants.UK_USERS_EMAIL, columnNames = {"email"})
})
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = AppConstants.MAX_USERNAME_LENGTH)
    private String username;

    @Column(nullable = false, length = AppConstants.MAX_EMAIL_LENGTH)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = AppConstants.MAX_ROLE_LENGTH)
    private String role = AppConstants.DEFAULT_USER_ROLE;

    // User preferences
    @Column(name = "genre_preference", nullable = false, length = AppConstants.MAX_GENRE_PREFERENCE_LENGTH,
            columnDefinition = "varchar(50) default 'GENERAL'")
    private String genrePreference = AppConstants.DEFAULT_GENRE_PREFERENCE;

    @Column(name = "language_preference", nullable = false, length = AppConstants.MAX_LANGUAGE_PREFERENCE_LENGTH,
            columnDefinition = "varchar(30) default 'English'")
    private String languagePreference = AppConstants.DEFAULT_LANGUAGE_PREFERENCE;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
