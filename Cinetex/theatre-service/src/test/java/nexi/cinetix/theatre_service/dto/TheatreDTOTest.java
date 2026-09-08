package nexi.cinetix.theatre_service.dto;

import nexi.cinetix.theatre_service.entity.Theatre;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TheatreDTOTest {

    @Test
    void fromEntity_null_returnsNull() {
        assertNull(TheatreDTO.fromEntity(null));
    }

    @Test
    void fromEntity_mapsAllFields() {
        Theatre entity = Theatre.builder()
                .theatreId(42L)
                .name("Grand Cinema")
                .city("Metropolis")
                .state("NY")
                .build();

        TheatreDTO dto = TheatreDTO.fromEntity(entity);

        assertNotNull(dto);
        assertEquals(42L, dto.getTheatreId());
        assertEquals("Grand Cinema", dto.getName());
        assertEquals("Metropolis", dto.getCity());
        assertEquals("NY", dto.getState());
    }

    @Test
    void toEntity_mapsAllFields() {
        TheatreDTO dto = TheatreDTO.builder()
                .theatreId(7L)
                .name("Indie House")
                .city("Gotham")
                .state("IL")
                .build();

        Theatre entity = dto.toEntity();

        assertNotNull(entity);
        assertEquals(7L, entity.getTheatreId());
        assertEquals("Indie House", entity.getName());
        assertEquals("Gotham", entity.getCity());
        assertEquals("IL", entity.getState());
    }

    @Test
    void roundTrip_entityToDtoToEntity_preservesFields() {
        Theatre original = Theatre.builder()
                .theatreId(88L)
                .name("Arcadia")
                .city("Springfield")
                .state("CA")
                .build();

        TheatreDTO dto = TheatreDTO.fromEntity(original);
        Theatre rebuilt = dto.toEntity();

        assertEquals(original.getTheatreId(), rebuilt.getTheatreId());
        assertEquals(original.getName(), rebuilt.getName());
        assertEquals(original.getCity(), rebuilt.getCity());
        assertEquals(original.getState(), rebuilt.getState());
    }

    @Test
    void builder_allArgs_thenModifySetters() {
        TheatreDTO dto = TheatreDTO.builder()
                .theatreId(5L)
                .name("Temp Name")
                .city("Temp City")
                .state("TS")
                .build();

        dto.setName("Final Name");
        dto.setCity("Final City");
        dto.setState("FS");

        assertEquals(5L, dto.getTheatreId());
        assertEquals("Final Name", dto.getName());
        assertEquals("Final City", dto.getCity());
        assertEquals("FS", dto.getState());
    }
}
