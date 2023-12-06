package life.offonoff.ab.repository.notice;

import life.offonoff.ab.domain.notice.VotingResultNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final NotificationJdbcRepository notificationJdbcRepository;

    @Override
    public void saveAll(List<VotingResultNotification> notifications) {
        notificationJdbcRepository.batchInsertVotingResultNotices(notifications);
    }
}
