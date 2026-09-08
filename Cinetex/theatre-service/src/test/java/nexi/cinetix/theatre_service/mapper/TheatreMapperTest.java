package nexi.cinetix.theatre_service.mapper;

import nexi.cinetix.theatre_service.dto.TheatreDTO;
import nexi.cinetix.theatre_service.entity.Theatre;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TheatreMapperTest {

    @InjectMocks
    private TheatreMapper theatreMapper;

    @Test
    void toDto_validEntity() {
        Theatre entity = Theatre.builder()
                .theatreId(1L)
                .name("Central Cinema")
                .city("Copenhagen")
                .state("Capital Region")
                .build();

        TheatreDTO result = theatreMapper.toDto(entity);

        assertNotNull(result);
        assertEquals(1L, result.getTheatreId());
        assertEquals("Central Cinema", result.getName());
        assertEquals("Copenhagen", result.getCity());
        assertEquals("Capital Region", result.getState());
    }

    @Test
    void toDto_nullEntity() {
        TheatreDTO result = theatreMapper.toDto(null);
        assertNull(result);
    }

    @Test
    void toDto_entityWithNullValues() {
        Theatre entity = Theatre.builder()
                .theatreId(null)
                .name(null)
                .city(null)
                .state(null)
                .build();

        TheatreDTO result = theatreMapper.toDto(entity);

        assertNotNull(result);
        assertNull(result.getTheatreId());
        assertNull(result.getName());
        assertNull(result.getCity());
        assertNull(result.getState());
    }

    @Test
    void toDto_entityWithEmptyValues() {
        Theatre entity = Theatre.builder()
                .theatreId(2L)
                .name("")
                .city("")
                .state("")
                .build();

        TheatreDTO result = theatreMapper.toDto(entity);

        assertNotNull(result);
        assertEquals(2L, result.getTheatreId());
        assertEquals("", result.getName());
        assertEquals("", result.getCity());
        assertEquals("", result.getState());
    }

    @Test
    void toEntity_validDto() {
        TheatreDTO dto = TheatreDTO.builder()
                .theatreId(1L)
                .name("Central Cinema")
                .city("Copenhagen")
                .state("Capital Region")
                .build();

        Theatre result = theatreMapper.toEntity(dto);

        assertNotNull(result);
        assertEquals(1L, result.getTheatreId());
        assertEquals("Central Cinema", result.getName());
        assertEquals("Copenhagen", result.getCity());
        assertEquals("Capital Region", result.getState());
    }

    @Test
    void toEntity_nullDto() {
        Theatre result = theatreMapper.toEntity(null);
        assertNull(result);
    }

    @Test
    void toEntity_dtoWithNullValues() {
        TheatreDTO dto = TheatreDTO.builder()
                .theatreId(null)
                .name(null)
                .city(null)
                .state(null)
                .build();

        Theatre result = theatreMapper.toEntity(dto);

        assertNotNull(result);
        assertNull(result.getTheatreId());
        assertNull(result.getName());
        assertNull(result.getCity());
        assertNull(result.getState());
    }

    @Test
    void toEntity_dtoWithEmptyValues() {
        TheatreDTO dto = TheatreDTO.builder()
                .theatreId(3L)
                .name("")
                .city("")
                .state("")
                .build();

        Theatre result = theatreMapper.toEntity(dto);

        assertNotNull(result);
        assertEquals(3L, result.getTheatreId());
        assertEquals("", result.getName());
        assertEquals("", result.getCity());
        assertEquals("", result.getState());
    }

    @Test
    void toDto_entityWithSpecialCharacters() {
        Theatre entity = Theatre.builder()
                .theatreId(4L)
                .name("Ålborg Kino")
                .city("Århus")
                .state("Øst Danmark")
                .build();

        TheatreDTO result = theatreMapper.toDto(entity);

        assertNotNull(result);
        assertEquals(4L, result.getTheatreId());
        assertEquals("Ålborg Kino", result.getName());
        assertEquals("Århus", result.getCity());
        assertEquals("Øst Danmark", result.getState());
    }

    @Test
    void toEntity_dtoWithSpecialCharacters() {
        TheatreDTO dto = TheatreDTO.builder()
                .theatreId(5L)
                .name("Ålborg Kino")
                .city("Århus")
                .state("Øst Danmark")
                .build();

        Theatre result = theatreMapper.toEntity(dto);

        assertNotNull(result);
        assertEquals(5L, result.getTheatreId());
        assertEquals("Ålborg Kino", result.getName());
        assertEquals("Århus", result.getCity());
        assertEquals("Øst Danmark", result.getState());
    }

    @Test
    void toDto_entityWithLongValues() {
        String longName = "A".repeat(255);
        String longCity = "B".repeat(100);
        String longState = "C".repeat(100);

        Theatre entity = Theatre.builder()
                .theatreId(6L)
                .name(longName)
                .city(longCity)
                .state(longState)
                .build();

        TheatreDTO result = theatreMapper.toDto(entity);

        assertNotNull(result);
        assertEquals(6L, result.getTheatreId());
        assertEquals(longName, result.getName());
        assertEquals(longCity, result.getCity());
        assertEquals(longState, result.getState());
    }

    @Test
    void toEntity_dtoWithLongValues() {
        String longName = "X".repeat(255);
        String longCity = "Y".repeat(100);
        String longState = "Z".repeat(100);

        TheatreDTO dto = TheatreDTO.builder()
                .theatreId(7L)
                .name(longName)
                .city(longCity)
                .state(longState)
                .build();

        Theatre result = theatreMapper.toEntity(dto);

        assertNotNull(result);
        assertEquals(7L, result.getTheatreId());
        assertEquals(longName, result.getName());
        assertEquals(longCity, result.getCity());
        assertEquals(longState, result.getState());
    }

    @Test
    void toDto_entityWithWhitespaceValues() {
        Theatre entity = Theatre.builder()
                .theatreId(8L)
                .name("   ")
                .city("   ")
                .state("   ")
                .build();

        TheatreDTO result = theatreMapper.toDto(entity);

        assertNotNull(result);
        assertEquals(8L, result.getTheatreId());
        assertEquals("   ", result.getName());
        assertEquals("   ", result.getCity());
        assertEquals("   ", result.getState());
    }

    @Test
    void toEntity_dtoWithWhitespaceValues() {
        TheatreDTO dto = TheatreDTO.builder()
                .theatreId(9L)
                .name("   ")
                .city("   ")
                .state("   ")
                .build();

        Theatre result = theatreMapper.toEntity(dto);

        assertNotNull(result);
        assertEquals(9L, result.getTheatreId());
        assertEquals("   ", result.getName());
        assertEquals("   ", result.getCity());
        assertEquals("   ", result.getState());
    }

    @Test
    void roundTrip_dtoToEntityToDto() {
        TheatreDTO originalDto = TheatreDTO.builder()
                .theatreId(10L)
                .name("Round Trip Cinema")
                .city("Test City")
                .state("Test State")
                .build();

        Theatre entity = theatreMapper.toEntity(originalDto);
        TheatreDTO resultDto = theatreMapper.toDto(entity);

        assertNotNull(resultDto);
        assertEquals(originalDto.getTheatreId(), resultDto.getTheatreId());
        assertEquals(originalDto.getName(), resultDto.getName());
        assertEquals(originalDto.getCity(), resultDto.getCity());
        assertEquals(originalDto.getState(), resultDto.getState());
    }

    @Test
    void roundTrip_entityToDtoToEntity() {
        Theatre originalEntity = Theatre.builder()
                .theatreId(11L)
                .name("Round Trip Entity Cinema")
                .city("Entity Test City")
                .state("Entity Test State")
                .build();

        TheatreDTO dto = theatreMapper.toDto(originalEntity);
        Theatre resultEntity = theatreMapper.toEntity(dto);

        assertNotNull(resultEntity);
        assertEquals(originalEntity.getTheatreId(), resultEntity.getTheatreId());
        assertEquals(originalEntity.getName(), resultEntity.getName());
        assertEquals(originalEntity.getCity(), resultEntity.getCity());
        assertEquals(originalEntity.getState(), resultEntity.getState());
    }
}
