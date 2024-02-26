package life.offonoff.ab.web;

import life.offonoff.ab.application.notification.NotificationService;
import life.offonoff.ab.web.common.aspect.auth.Authorized;
import life.offonoff.ab.web.response.notification.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("")
    public ResponseEntity<List<NotificationResponse>> getNotifications(@Authorized Long memberId) {
        return ResponseEntity.ok(notificationService.findAllByReceiverId(memberId));
    }
}
