package nexi.cinetix.theatre_service.exception;

public class ShowtimeNotFoundException extends RuntimeException {
    public ShowtimeNotFoundException(Long id) { super("Showtime not found: " + id); }
}

