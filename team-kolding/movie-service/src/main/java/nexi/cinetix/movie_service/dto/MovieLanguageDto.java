package nexi.cinetix.movie_service.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieLanguageDto {
    private Integer id;
    private Integer movieId;
    private Integer languageId;
}

