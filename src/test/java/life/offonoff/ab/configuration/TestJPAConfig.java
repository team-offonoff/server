package life.offonoff.ab.configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import life.offonoff.ab.repository.notfication.NotificationJdbcRepositoryImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@TestConfiguration
public class TestJPAConfig {

    @PersistenceContext
    private EntityManager em;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }

    @Bean
    public NotificationJdbcRepositoryImpl notificationJdbcRepository(DataSource dataSource) {
        return new NotificationJdbcRepositoryImpl(dataSource);
    }
}
