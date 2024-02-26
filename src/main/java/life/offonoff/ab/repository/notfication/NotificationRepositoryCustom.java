package life.offonoff.ab.repository.notfication;

import life.offonoff.ab.domain.notification.VoteResultNotification;

import java.util.List;

public interface NotificationRepositoryCustom {

    void saveAll(List<VoteResultNotification> notifications);
}
