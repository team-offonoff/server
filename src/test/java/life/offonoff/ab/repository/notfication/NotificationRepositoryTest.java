package life.offonoff.ab.repository.notfication;

import jakarta.persistence.EntityManager;
import life.offonoff.ab.configuration.TestJPAConfig;
import life.offonoff.ab.domain.TestEntityUtil.TestMember;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.notification.DefaultNotification;
import life.offonoff.ab.domain.notification.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestJPAConfig.class)
class NotificationRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    NotificationRepository notificationRepository;

    @Test
    void findByReceiverId() {
        // given
        Member receiver = TestMember.builder()
                                    .build().buildMember();
        em.persist(receiver);

        DefaultNotification notification = new DefaultNotification(receiver, "title", "content");
        notificationRepository.save(notification);

        // when
        List<Notification> notifications = notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(receiver.getId());

        // then
        assertThat(notifications.size()).isGreaterThan(0);
    }
}