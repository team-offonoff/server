package life.offonoff.ab.repository.notice;

import life.offonoff.ab.domain.notice.VotingResultNotification;

import java.util.List;

public interface NotificationRepositoryCustom {

    void saveAll(List<VotingResultNotification> notifications);
}
