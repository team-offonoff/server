package life.offonoff.ab.repository.notfication;

import life.offonoff.ab.domain.notification.VoteResultNotification;

import java.util.List;

public interface NotificationJdbcRepository {

    void saveVoteResultNotificationsInBatch(List<VoteResultNotification> notifications);

    void deleteAllByTopicId(Long topicId);
}
