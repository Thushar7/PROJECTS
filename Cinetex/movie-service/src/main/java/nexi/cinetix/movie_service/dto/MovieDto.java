package nexi.cinetix.movie_service.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private Integer movieId;
    private String title;
    private Integer duration; // minutes
    private LocalDate releaseDate;
    private String description;
    private String posterBase64; // Base64 encoded poster image (optional when listing)
    private String posterContentType; // MIME type e.g. image/jpeg

    // Represent relationships by IDs to avoid deep nesting / cycles
    @Builder.Default
    private List<Integer> genreIds = List.of();

    @Builder.Default
    private List<Integer> languageIds = List.of();
}
