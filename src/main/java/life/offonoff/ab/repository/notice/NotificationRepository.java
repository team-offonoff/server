package life.offonoff.ab.repository.notice;

import life.offonoff.ab.domain.notice.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {
}
