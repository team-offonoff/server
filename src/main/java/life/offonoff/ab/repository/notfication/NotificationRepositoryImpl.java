package life.offonoff.ab.repository.notfication;

import life.offonoff.ab.domain.notification.VoteResultNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final NotificationJdbcRepositoryImpl notificationJdbcRepository;

    @Override
    public void saveAll(List<VoteResultNotification> notifications) {
        notificationJdbcRepository.saveVoteResultNotificationsInBatch(notifications);
    }
}
