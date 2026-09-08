package nexi.cinetix.theatre_service.exception;

public class TheatreNotFoundException extends RuntimeException {
    public TheatreNotFoundException(Long id) { super("Theatre not found: " + id); }
}

