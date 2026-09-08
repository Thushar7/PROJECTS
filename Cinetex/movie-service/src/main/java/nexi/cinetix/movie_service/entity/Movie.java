package nexi.cinetix.movie_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Integer movieId;

    private String title;

    // duration in minutes
    private Integer duration;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "poster_url", columnDefinition = "LONGBLOB")
    private byte[] posterData; // Binary image data stored in LONGBLOB

    @Column(name = "poster_content_type")
    private String posterContentType; // MIME type of the stored poster

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<MovieGenre> movieGenres = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<MovieLanguage> movieLanguages = new HashSet<>();

    // Convenience helpers
    public void addGenre(Genre genre) {
        MovieGenre mg = new MovieGenre();
        mg.setMovie(this);
        mg.setGenre(genre);
        movieGenres.add(mg);
    }

    public void addLanguage(Language language) {
        MovieLanguage ml = new MovieLanguage();
        ml.setMovie(this);
        ml.setLanguage(language);
        movieLanguages.add(ml);
    }
}
