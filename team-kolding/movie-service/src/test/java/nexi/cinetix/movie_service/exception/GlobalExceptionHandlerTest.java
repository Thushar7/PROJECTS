package nexi.cinetix.movie_service.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_shouldReturn404Response() {
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("Movie not found");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/movies/1");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(ex, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Movie not found", response.getBody().get("message"));
        assertEquals(404, response.getBody().get("status"));
        assertEquals("Not Found", response.getBody().get("error"));
        assertEquals("/movies/1", response.getBody().get("path"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void handleBadRequest_shouldReturn400Response() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("Invalid input");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/movies");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleBadRequest(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid input", response.getBody().get("message"));
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Bad Request", response.getBody().get("error"));
        assertEquals("/movies", response.getBody().get("path"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void handleGeneric_shouldReturn500Response() {
        // Arrange
        Exception ex = new Exception("Unexpected error");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/movies");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleGeneric(ex, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error", response.getBody().get("message"));
        assertEquals(500, response.getBody().get("status"));
        assertEquals("Internal Server Error", response.getBody().get("error"));
        assertEquals("/movies", response.getBody().get("path"));
        assertNotNull(response.getBody().get("timestamp"));
    }
}
