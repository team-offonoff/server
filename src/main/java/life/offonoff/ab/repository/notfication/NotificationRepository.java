package life.offonoff.ab.repository.notfication;

import life.offonoff.ab.domain.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>,
                                                NotificationRepositoryCustom,
                                                NotificationJdbcRepository {

    List<Notification> findAllByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    Integer countByCheckedFalseAndReceiverId(Long receiverId);
}

