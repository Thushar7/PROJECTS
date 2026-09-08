package nexi.cinetix.movie_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexi.cinetix.movie_service.dto.GenreDto;
import nexi.cinetix.movie_service.entity.Genre;
import nexi.cinetix.movie_service.exception.ResourceNotFoundException;
import nexi.cinetix.movie_service.mapper.GenreMapper;
import nexi.cinetix.movie_service.repository.GenreRepository;
import nexi.cinetix.movie_service.service.GenreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Override
    public List<GenreDto> getAll() {
        log.debug("Fetching all genres");
        return genreRepository.findAll().stream().map(genreMapper::toDto).toList();
    }

    @Override
    public GenreDto get(Integer id) {
        log.debug("Fetching genre id={}", id);
        return genreMapper.toDto(find(id));
    }

    @Override
    public GenreDto create(GenreDto dto) {
        log.debug("Creating genre payload={}", dto);
        if (dto.getName() == null || dto.getName().isBlank()) {
            log.warn("Attempt to create genre with blank name");
            throw new IllegalArgumentException("Genre name required");
        }
        if (genreRepository.existsByNameIgnoreCase(dto.getName())) {
            log.warn("Duplicate genre creation attempted name={}", dto.getName());
            throw new IllegalArgumentException("Genre already exists: " + dto.getName());
        }
        Genre genre = Genre.builder().name(dto.getName().trim()).build();
        Genre saved = genreRepository.save(genre);
        log.info("Genre created id={} name={}", saved.getGenreId(), saved.getName());
        return genreMapper.toDto(saved);
    }

    @Override
    public GenreDto update(Integer id, GenreDto dto) {
        log.debug("Updating genre id={} payload={}", id, dto);
        Genre genre = find(id);
        if (dto.getName() == null || dto.getName().isBlank()) {
            log.warn("Attempt to update genre id={} with blank name", id);
            throw new IllegalArgumentException("Genre name required");
        }
        String newName = dto.getName().trim();
        if (!genre.getName().equalsIgnoreCase(newName) && genreRepository.existsByNameIgnoreCase(newName)) {
            log.warn("Duplicate genre name on update id={} name={}", id, newName);
            throw new IllegalArgumentException("Genre already exists: " + newName);
        }
        genre.setName(newName);
        Genre saved = genreRepository.save(genre);
        log.info("Genre updated id={} name={}", saved.getGenreId(), saved.getName());
        return genreMapper.toDto(saved);
    }

    @Override
    public void delete(Integer id) {
        log.debug("Deleting genre id={}", id);
        Genre genre = find(id);
        genreRepository.delete(genre);
        log.info("Genre deleted id={}", id);
    }

    private Genre find(Integer id) {
        return genreRepository.findById(id).orElseThrow(() -> {
            log.warn("Genre not found id={}", id);
            return new ResourceNotFoundException("Genre not found: " + id);
        });
    }
}
