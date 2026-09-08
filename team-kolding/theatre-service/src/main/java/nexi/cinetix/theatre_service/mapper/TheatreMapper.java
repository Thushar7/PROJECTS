package nexi.cinetix.theatre_service.mapper;

import nexi.cinetix.theatre_service.dto.TheatreDTO;
import nexi.cinetix.theatre_service.entity.Theatre;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between Theatre entity and TheatreDTO.
 */
@Component
public class TheatreMapper {

    public TheatreDTO toDto(Theatre entity) {
        if (entity == null) return null;

        return TheatreDTO.builder()
                .theatreId(entity.getTheatreId())
                .name(entity.getName())
                .city(entity.getCity())
                .state(entity.getState())
                .build();
    }

    public Theatre toEntity(TheatreDTO dto) {
        if (dto == null) return null;

        return Theatre.builder()
                .theatreId(dto.getTheatreId())
                .name(dto.getName())
                .city(dto.getCity())
                .state(dto.getState())
                .build();
    }
}