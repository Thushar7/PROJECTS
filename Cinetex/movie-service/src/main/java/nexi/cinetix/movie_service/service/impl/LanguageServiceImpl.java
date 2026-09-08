package nexi.cinetix.movie_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexi.cinetix.movie_service.dto.LanguageDto;
import nexi.cinetix.movie_service.entity.Language;
import nexi.cinetix.movie_service.exception.ResourceNotFoundException;
import nexi.cinetix.movie_service.mapper.LanguageMapper;
import nexi.cinetix.movie_service.repository.LanguageRepository;
import nexi.cinetix.movie_service.service.LanguageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;
    private final LanguageMapper languageMapper;

    @Override
    public List<LanguageDto> getAll() {
        log.debug("Fetching all languages");
        return languageRepository.findAll().stream().map(languageMapper::toDto).toList();
    }

    @Override
    public LanguageDto get(Integer id) {
        log.debug("Fetching language id={}", id);
        return languageMapper.toDto(find(id));
    }

    @Override
    public LanguageDto create(LanguageDto dto) {
        log.debug("Creating language payload={}", dto);
        if (dto.getName() == null || dto.getName().isBlank()) {
            log.warn("Attempt to create language with blank name");
            throw new IllegalArgumentException("Language name required");
        }
        if (languageRepository.existsByNameIgnoreCase(dto.getName())) {
            log.warn("Duplicate language creation attempted name={}", dto.getName());
            throw new IllegalArgumentException("Language already exists: " + dto.getName());
        }
        Language language = Language.builder().name(dto.getName().trim()).build();
        Language saved = languageRepository.save(language);
        log.info("Language created id={} name={}", saved.getLanguageId(), saved.getName());
        return languageMapper.toDto(saved);
    }

    @Override
    public LanguageDto update(Integer id, LanguageDto dto) {
        log.debug("Updating language id={} payload={}", id, dto);
        Language language = find(id);
        if (dto.getName() == null || dto.getName().isBlank()) {
            log.warn("Attempt to update language id={} with blank name", id);
            throw new IllegalArgumentException("Language name required");
        }
        String newName = dto.getName().trim();
        if (!language.getName().equalsIgnoreCase(newName) && languageRepository.existsByNameIgnoreCase(newName)) {
            log.warn("Duplicate language name on update id={} name={}", id, newName);
            throw new IllegalArgumentException("Language already exists: " + newName);
        }
        language.setName(newName);
        Language saved = languageRepository.save(language);
        log.info("Language updated id={} name={}", saved.getLanguageId(), saved.getName());
        return languageMapper.toDto(saved);
    }

    @Override
    public void delete(Integer id) {
        log.debug("Deleting language id={}", id);
        Language language = find(id);
        languageRepository.delete(language);
        log.info("Language deleted id={}", id);
    }

    private Language find(Integer id) {
        return languageRepository.findById(id).orElseThrow(() -> {
            log.warn("Language not found id={}", id);
            return new ResourceNotFoundException("Language not found: " + id);
        });
    }
}
