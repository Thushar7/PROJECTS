package nexi.cinetix.theatre_service.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TheatreTest {

    @Test
    void builder_allFields() {
        Theatre theatre = Theatre.builder()
                .theatreId(1L)
                .name("Central Cinema")
                .city("Copenhagen")
                .state("Capital Region")
                .build();

        assertEquals(1L, theatre.getTheatreId());
        assertEquals("Central Cinema", theatre.getName());
        assertEquals("Copenhagen", theatre.getCity());
        assertEquals("Capital Region", theatre.getState());
    }

    @Test
    void builder_nullValues() {
        Theatre theatre = Theatre.builder()
                .theatreId(null)
                .name(null)
                .city(null)
                .state(null)
                .build();

        assertNull(theatre.getTheatreId());
        assertNull(theatre.getName());
        assertNull(theatre.getCity());
        assertNull(theatre.getState());
    }

    @Test
    void builder_emptyValues() {
        Theatre theatre = Theatre.builder()
                .theatreId(2L)
                .name("")
                .city("")
                .state("")
                .build();

        assertEquals(2L, theatre.getTheatreId());
        assertEquals("", theatre.getName());
        assertEquals("", theatre.getCity());
        assertEquals("", theatre.getState());
    }

    @Test
    void settersAndGetters() {
        Theatre theatre = new Theatre();

        theatre.setTheatreId(3L);
        theatre.setName("Test Cinema");
        theatre.setCity("Test City");
        theatre.setState("Test State");

        assertEquals(3L, theatre.getTheatreId());
        assertEquals("Test Cinema", theatre.getName());
        assertEquals("Test City", theatre.getCity());
        assertEquals("Test State", theatre.getState());
    }

    @Test
    void settersWithNullValues() {
        Theatre theatre = new Theatre();

        theatre.setTheatreId(null);
        theatre.setName(null);
        theatre.setCity(null);
        theatre.setState(null);

        assertNull(theatre.getTheatreId());
        assertNull(theatre.getName());
        assertNull(theatre.getCity());
        assertNull(theatre.getState());
    }

    @Test
    void noArgsConstructor() {
        Theatre theatre = new Theatre();

        assertNull(theatre.getTheatreId());
        assertNull(theatre.getName());
        assertNull(theatre.getCity());
        assertNull(theatre.getState());
    }

    @Test
    void allArgsConstructor() {
        Theatre theatre = new Theatre(4L, "All Args Cinema", "All Args City", "All Args State");

        assertEquals(4L, theatre.getTheatreId());
        assertEquals("All Args Cinema", theatre.getName());
        assertEquals("All Args City", theatre.getCity());
        assertEquals("All Args State", theatre.getState());
    }

    @Test
    void equals_sameObject() {
        Theatre theatre = Theatre.builder()
                .theatreId(1L)
                .name("Cinema")
                .city("City")
                .state("State")
                .build();

        assertEquals(theatre, theatre);
    }


    @Test
    void equals_differentObjects() {
        Theatre theatre1 = Theatre.builder()
                .theatreId(1L)
                .name("Cinema 1")
                .city("City")
                .state("State")
                .build();

        Theatre theatre2 = Theatre.builder()
                .theatreId(2L)
                .name("Cinema 2")
                .city("City")
                .state("State")
                .build();

        assertNotEquals(theatre1, theatre2);
    }

    @Test
    void equals_nullObject() {
        Theatre theatre = Theatre.builder()
                .theatreId(1L)
                .name("Cinema")
                .build();

        assertNotEquals(null, theatre);
    }

    @Test
    void equals_differentClass() {
        Theatre theatre = Theatre.builder()
                .theatreId(1L)
                .name("Cinema")
                .build();

        assertNotEquals("Not a theatre", theatre);
    }



    @Test
    void hashCode_differentObjects() {
        Theatre theatre1 = Theatre.builder()
                .theatreId(1L)
                .name("Cinema 1")
                .build();

        Theatre theatre2 = Theatre.builder()
                .theatreId(2L)
                .name("Cinema 2")
                .build();

        assertNotEquals(theatre1.hashCode(), theatre2.hashCode());
    }


    @Test
    void builder_partialFields() {
        Theatre theatre = Theatre.builder()
                .theatreId(5L)
                .name("Partial Cinema")
                .build();

        assertEquals(5L, theatre.getTheatreId());
        assertEquals("Partial Cinema", theatre.getName());
        assertNull(theatre.getCity());
        assertNull(theatre.getState());
    }

    @Test
    void modifyAfterCreation() {
        Theatre theatre = Theatre.builder()
                .theatreId(6L)
                .name("Original Name")
                .city("Original City")
                .state("Original State")
                .build();

        theatre.setName("Modified Name");
        theatre.setCity("Modified City");
        theatre.setState("Modified State");

        assertEquals(6L, theatre.getTheatreId());
        assertEquals("Modified Name", theatre.getName());
        assertEquals("Modified City", theatre.getCity());
        assertEquals("Modified State", theatre.getState());
    }

    @Test
    void equals_oneWithNullOneWithoutNull() {
        Theatre theatre1 = Theatre.builder()
                .theatreId(1L)
                .name("Cinema")
                .city("City")
                .state("State")
                .build();

        Theatre theatre2 = Theatre.builder()
                .theatreId(1L)
                .name(null)
                .city(null)
                .state(null)
                .build();

        assertNotEquals(theatre1, theatre2);
    }

    @Test
    void withSpecialCharacters() {
        Theatre theatre = Theatre.builder()
                .theatreId(7L)
                .name("Ålborg Kino")
                .city("Århus")
                .state("Øst Danmark")
                .build();

        assertEquals(7L, theatre.getTheatreId());
        assertEquals("Ålborg Kino", theatre.getName());
        assertEquals("Århus", theatre.getCity());
        assertEquals("Øst Danmark", theatre.getState());
    }

    @Test
    void withLongValues() {
        String longName = "A".repeat(255);
        String longCity = "B".repeat(100);
        String longState = "C".repeat(100);

        Theatre theatre = Theatre.builder()
                .theatreId(8L)
                .name(longName)
                .city(longCity)
                .state(longState)
                .build();

        assertEquals(8L, theatre.getTheatreId());
        assertEquals(longName, theatre.getName());
        assertEquals(longCity, theatre.getCity());
        assertEquals(longState, theatre.getState());
    }
}
