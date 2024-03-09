package life.offonoff.ab.repository.notfication;

import life.offonoff.ab.application.service.request.NotificationRequest;
import life.offonoff.ab.domain.notification.Notification;
import life.offonoff.ab.domain.notification.VoteResultNotification;

import java.util.List;

public interface NotificationRepositoryCustom {

    void saveAll(List<VoteResultNotification> notifications);

    List<Notification> findAllOrderByCreatedAtDesc(Long receiverId, NotificationRequest request);
}
