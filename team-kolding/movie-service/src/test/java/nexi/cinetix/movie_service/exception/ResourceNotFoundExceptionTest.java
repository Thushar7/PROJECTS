package nexi.cinetix.movie_service.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Test
    void resourceNotFoundException_withMessage() {
        String message = "Movie with id 123 not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void resourceNotFoundException_inheritanceFromRuntimeException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Test message");
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void resourceNotFoundException_nullMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException(null);
        assertNull(exception.getMessage());
    }

    @Test
    void resourceNotFoundException_emptyMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("");
        assertEquals("", exception.getMessage());
    }

    @Test
    void resourceNotFoundException_longMessage() {
        String longMessage = "This is a very long error message that describes a specific resource not found scenario with detailed information about the missing resource";
        ResourceNotFoundException exception = new ResourceNotFoundException(longMessage);
        assertEquals(longMessage, exception.getMessage());
    }
}
