package life.offonoff.ab.repository.notice;

import life.offonoff.ab.domain.notice.VoteResultNotification;

import java.util.List;

public interface NotificationRepositoryCustom {

    void saveAll(List<VoteResultNotification> notifications);
}
