package nexi.cinetix.movie_service.service;

import nexi.cinetix.movie_service.dto.GenreDto;

import java.util.List;

public interface GenreService {
    List<GenreDto> getAll();
    GenreDto get(Integer id);
    GenreDto create(GenreDto dto);
    GenreDto update(Integer id, GenreDto dto);
    void delete(Integer id);
}
