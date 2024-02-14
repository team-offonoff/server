package life.offonoff.ab.repository.notice;

import life.offonoff.ab.domain.notice.VoteResultNotification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static life.offonoff.ab.domain.notice.NotificationType.*;

@Repository
public class NotificationJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public NotificationJdbcRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void batchInsertVoteResultNotifications(List<VoteResultNotification> notifications) {

        Timestamp curTimeStamp = Timestamp.valueOf(LocalDateTime.now());

        String query = "insert into notification (member_id, notification_type, voting_result_id, created_at, updated_at)" +
                "values (?, ?, ?, ?, ?);";

        jdbcTemplate.batchUpdate(query,
                                 notifications,
                                 1000,
                                 (pstmt, n) -> {
                                     pstmt.setLong(1, n.getReceiver().getId());
                                     pstmt.setString(2, VOTE_RESULT_NOTIFICATION);
                                     pstmt.setLong(3, n.getVoteResult().getId());
                                     pstmt.setTimestamp(4, curTimeStamp);
                                     pstmt.setTimestamp(5, curTimeStamp);
                                 });
    }

}
