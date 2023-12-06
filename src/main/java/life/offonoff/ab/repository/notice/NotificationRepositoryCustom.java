package life.offonoff.ab.repository.notice;

import life.offonoff.ab.domain.notice.Notification;
import life.offonoff.ab.domain.notice.VotingResultNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepositoryCustom {

    void saveAll(List<VotingResultNotification> notifications);
}
