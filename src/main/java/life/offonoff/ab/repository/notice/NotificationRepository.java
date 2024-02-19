package life.offonoff.ab.repository.notice;

import life.offonoff.ab.domain.notice.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {
    List<Notification> findAllByReceiverIdOrderByCreatedAtDesc(Long receiverId);
}

