package nexi.cinetix.theatre_service.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TheatreNotFoundExceptionTest {

    @Test
    void constructor_withTheatreId() {
        Long theatreId = 123L;

        TheatreNotFoundException exception = new TheatreNotFoundException(theatreId);

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("123"));
        assertNull(exception.getCause());
    }

    @Test
    void constructor_withNullTheatreId() {
        TheatreNotFoundException exception = new TheatreNotFoundException(null);

        assertNotNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void constructor_withZeroTheatreId() {
        TheatreNotFoundException exception = new TheatreNotFoundException(0L);

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("0"));
        assertNull(exception.getCause());
    }

    @Test
    void constructor_withNegativeTheatreId() {
        TheatreNotFoundException exception = new TheatreNotFoundException(-1L);

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("-1"));
        assertNull(exception.getCause());
    }

    @Test
    void constructor_withLargeTheatreId() {
        Long largeId = Long.MAX_VALUE;

        TheatreNotFoundException exception = new TheatreNotFoundException(largeId);

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains(largeId.toString()));
        assertNull(exception.getCause());
    }

    @Test
    void isRuntimeException() {
        TheatreNotFoundException exception = new TheatreNotFoundException(123L);

        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void throwException() {
        Long theatreId = 999L;

        assertThrows(TheatreNotFoundException.class, () -> {
            throw new TheatreNotFoundException(theatreId);
        });
    }

    @Test
    void catchException() {
        Long theatreId = 456L;

        try {
            throw new TheatreNotFoundException(theatreId);
        } catch (TheatreNotFoundException e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("456"));
        }
    }

    @Test
    void exceptionMessage_containsTheatreId() {
        Long theatreId = 789L;

        TheatreNotFoundException exception = new TheatreNotFoundException(theatreId);

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("789"));
        assertTrue(exception.getMessage().toLowerCase().contains("theatre"));
        assertTrue(exception.getMessage().toLowerCase().contains("not found"));
    }

    @Test
    void multipleExceptions_withDifferentIds() {
        TheatreNotFoundException exception1 = new TheatreNotFoundException(1L);
        TheatreNotFoundException exception2 = new TheatreNotFoundException(2L);

        assertNotEquals(exception1.getMessage(), exception2.getMessage());
        assertTrue(exception1.getMessage().contains("1"));
        assertTrue(exception2.getMessage().contains("2"));
    }

    @Test
    void exceptionMessage_consistency() {
        Long theatreId = 100L;

        TheatreNotFoundException exception1 = new TheatreNotFoundException(theatreId);
        TheatreNotFoundException exception2 = new TheatreNotFoundException(theatreId);

        assertEquals(exception1.getMessage(), exception2.getMessage());
    }
}
