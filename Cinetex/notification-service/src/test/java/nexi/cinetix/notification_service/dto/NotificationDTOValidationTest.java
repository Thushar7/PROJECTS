package nexi.cinetix.notification_service.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import nexi.cinetix.notification_service.enums.NotificationEvent;
import nexi.cinetix.notification_service.enums.NotificationType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void validDtoHasNoViolations() {
        NotificationDTO dto = new NotificationDTO();
        dto.setUserId(1L);
        dto.setBookingId(2L);
        dto.setEvent(NotificationEvent.BOOKING_CONFIRMED);
        dto.setType(NotificationType.USER);
        Set<ConstraintViolation<NotificationDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void missingRequiredFields() {
        NotificationDTO dto = new NotificationDTO();
        Set<ConstraintViolation<NotificationDTO>> violations = validator.validate(dto);
        // Required: userId, event, type
        assertThat(violations.size()).isGreaterThanOrEqualTo(3);
    }
}
