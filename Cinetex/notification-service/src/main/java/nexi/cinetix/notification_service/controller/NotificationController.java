package nexi.cinetix.notification_service.controller;

import lombok.RequiredArgsConstructor;
import nexi.cinetix.notification_service.dto.NotificationResponseDTO;
import nexi.cinetix.notification_service.entity.Notification;
import nexi.cinetix.notification_service.mapper.NotificationMapper;
import nexi.cinetix.notification_service.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getForUser(@PathVariable Long userId) {
        List<NotificationResponseDTO> list = notificationService.getNotificationsForUser(userId)
                .stream().map(notificationMapper::toResponseDto).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> getById(@PathVariable Long id) {
        return notificationService.getById(id)
                .map(notificationMapper::toResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/admin")
    public ResponseEntity<List<NotificationResponseDTO>> getAdminNotifications() {
        List<NotificationResponseDTO> adminNotifications = notificationService.getAdminNotifications()
                .stream()
                .map(notificationMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(adminNotifications);
    }
}
