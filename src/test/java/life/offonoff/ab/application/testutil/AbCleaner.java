package life.offonoff.ab.application.testutil;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AbCleaner {
    private final JdbcTemplate jdbcTemplate;
    private List<String> truncateQueries;

    @PostConstruct
    public void loadTruncateQueries(){
        truncateQueries = jdbcTemplate.queryForList(
                "SELECT Concat('TRUNCATE TABLE ', TABLE_NAME, ';') AS q FROM INFORMATION_SCHEMA.TABLES " +
                        "WHERE TABLE_SCHEMA IN ('PUBLIC', 'localab', 'ab')",
                String.class);
    }

    @Transactional
    public void cleanTables() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        truncateQueries.forEach(jdbcTemplate::execute);
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }
}
