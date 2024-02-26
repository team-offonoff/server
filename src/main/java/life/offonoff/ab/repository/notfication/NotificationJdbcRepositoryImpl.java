package life.offonoff.ab.repository.notfication;

import life.offonoff.ab.domain.notification.VoteResultNotification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static life.offonoff.ab.domain.notification.NotificationType.*;

@Repository
public class NotificationJdbcRepositoryImpl implements NotificationJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public NotificationJdbcRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void saveVoteResultNotificationsInBatch(List<VoteResultNotification> notifications) {

        Timestamp curTimeStamp = Timestamp.valueOf(LocalDateTime.now());

        String query = "insert into notification (member_id, notification_type, topic_id, vote_result_id, created_at, updated_at)" +
                "values (?, ?, ?, ?, ?, ?);";

        jdbcTemplate.batchUpdate(query,
                                 notifications,
                                 1000,
                                 (pstmt, n) -> {
                                     pstmt.setLong(1, n.getReceiver().getId());
                                     pstmt.setString(2, VOTE_RESULT_NOTIFICATION);
                                     pstmt.setLong(3, n.getVoteResult().getTopicId());
                                     pstmt.setLong(4, n.getVoteResult().getId());
                                     pstmt.setTimestamp(5, curTimeStamp);
                                     pstmt.setTimestamp(6, curTimeStamp);
                                 });
    }

    @Override
    public void deleteAllByTopicId(Long topicId) {
        String query = "delete from notification where topic_id = ?";

        jdbcTemplate.update(query, topicId);
    }

}
