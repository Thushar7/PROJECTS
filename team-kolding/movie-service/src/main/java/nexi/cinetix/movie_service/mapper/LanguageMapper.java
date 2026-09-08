package nexi.cinetix.movie_service.mapper;

import nexi.cinetix.movie_service.dto.LanguageDto;
import nexi.cinetix.movie_service.entity.Language;
import org.springframework.stereotype.Component;

@Component
public class LanguageMapper {

    public LanguageDto toDto(Language language) {
        return LanguageDto.builder()
                .languageId(language.getLanguageId())
                .name(language.getName())
                .build();
    }
}