package nexi.cinetix.theatre_service.service;

import lombok.RequiredArgsConstructor;
import nexi.cinetix.theatre_service.dto.TheatreDTO;
import nexi.cinetix.theatre_service.entity.Theatre;
import nexi.cinetix.theatre_service.exception.TheatreNotFoundException;
import nexi.cinetix.theatre_service.mapper.TheatreMapper;
import nexi.cinetix.theatre_service.repository.TheatreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TheatreService {
    private final TheatreRepository repo;
    private final TheatreMapper theatreMapper;

    public List<TheatreDTO> list(String state, String city) {
        List<Theatre> theatres;
        if (state != null && city != null) {
            theatres = repo.findByStateIgnoreCaseAndCityIgnoreCase(state, city);
        } else if (state != null) {
            theatres = repo.findByStateIgnoreCase(state);
        } else if (city != null) {
            theatres = repo.findByCityIgnoreCase(city);
        } else {
            theatres = repo.findAll();
        }
        return theatres.stream().map(theatreMapper::toDto).toList();
    }

    public TheatreDTO get(Long id) { return theatreMapper.toDto(find(id)); }

    @Transactional
    public TheatreDTO create(TheatreDTO dto) {
        dto.setTheatreId(null); // ensure new entity
        Theatre saved = repo.save(theatreMapper.toEntity(dto));
        return theatreMapper.toDto(saved);
    }

    @Transactional
    public TheatreDTO update(Long id, TheatreDTO dto) {
        Theatre existing = find(id);
        existing.setName(dto.getName());
        existing.setCity(dto.getCity());
        existing.setState(dto.getState());
        return theatreMapper.toDto(existing);
    }

    @Transactional
    public void delete(Long id) { repo.delete(find(id)); }

    private Theatre find(Long id) { return repo.findById(id).orElseThrow(() -> new TheatreNotFoundException(id)); }
}
