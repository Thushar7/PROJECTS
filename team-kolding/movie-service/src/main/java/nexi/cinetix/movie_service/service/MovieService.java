package nexi.cinetix.movie_service.service;

import nexi.cinetix.movie_service.dto.MovieDto;
import nexi.cinetix.movie_service.dto.PageResponse;

import java.util.List;

public interface MovieService {
    List<MovieDto> getAll();
    List<MovieDto> getAll(int page); // zero-based page index, fixed size 10
    MovieDto get(Integer id);
    List<MovieDto> searchByTitle(String titlePart);
    MovieDto create(MovieDto dto);
    MovieDto update(Integer id, MovieDto dto);
    void delete(Integer id);
    MovieDto addGenres(Integer movieId, List<Integer> genreIds);
    MovieDto removeGenre(Integer movieId, Integer genreId);
    MovieDto addLanguages(Integer movieId, List<Integer> languageIds);
    MovieDto removeLanguage(Integer movieId, Integer languageId);
    List<MovieDto> getAllFiltered(List<Integer> genreIds, List<Integer> languageIds);
    List<MovieDto> getAllFiltered(List<Integer> genreIds, List<Integer> languageIds, int page); // paged variant
    PageResponse<MovieDto> getPage(int page, int size, List<Integer> genreIds, List<Integer> languageIds); // new metadata response
    // Poster operations
    void updatePoster(Integer movieId, byte[] data, String contentType);
    byte[] getPosterData(Integer movieId);
    String getPosterContentType(Integer movieId);
}
