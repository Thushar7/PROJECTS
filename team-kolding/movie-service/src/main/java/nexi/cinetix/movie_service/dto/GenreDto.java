package nexi.cinetix.movie_service.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenreDto {
    private Integer genreId;
    private String name;
}

