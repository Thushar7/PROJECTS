package nexi.cinetix.movie_service.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguageDto {
    private Integer languageId;
    private String name;
}

