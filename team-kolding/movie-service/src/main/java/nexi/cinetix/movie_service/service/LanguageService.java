package nexi.cinetix.movie_service.service;

import nexi.cinetix.movie_service.dto.LanguageDto;

import java.util.List;

public interface LanguageService {
    List<LanguageDto> getAll();
    LanguageDto get(Integer id);
    LanguageDto create(LanguageDto dto);
    LanguageDto update(Integer id, LanguageDto dto);
    void delete(Integer id);
}
