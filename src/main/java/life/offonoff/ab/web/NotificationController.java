package life.offonoff.ab.web;

import life.offonoff.ab.application.notification.NotificationService;
import life.offonoff.ab.application.service.request.NotificationRequest;
import life.offonoff.ab.web.common.aspect.auth.Authorized;
import life.offonoff.ab.web.response.notification.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("")
    public ResponseEntity<List<NotificationResponse>> getNotifications(@Authorized Long memberId,
                                                                       NotificationRequest request) {
        return ResponseEntity.ok(notificationService.findAllByReceiverId(memberId, request));
    }

    @GetMapping("/counts/unread")
    public ResponseEntity<Integer> getNotificationCounts(@Authorized Long memberId) {
        return ResponseEntity.ok(notificationService.countUncheckedByReceiverId(memberId));
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> readNotification(@Authorized Long memberId,
                                                  @PathVariable Long notificationId) {
        notificationService.readNotification(memberId, notificationId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
    }
}
