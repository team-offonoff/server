package life.offonoff.ab.application.notification;

import jakarta.persistence.EntityManager;
import life.offonoff.ab.domain.TestEntityUtil;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.web.response.notification.NotificationResponse;
import life.offonoff.ab.web.response.notification.message.VoteCountOnTopicNotificationMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static life.offonoff.ab.domain.TestEntityUtil.createRandomMember;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@DisplayName("NoticeService 스프링 컨테이너 통합테스트")
public class NotificationServiceIntegrationTest {

    @Autowired
    NotificationService notificationService;
    @Value("${ab.notification.vote_on_topic.count_unit}")
    int voteCountUnit;

    @Autowired
    EntityManager em;


    @Test
    @DisplayName("Vote Count 알림 기준에 해당하면 알림을 생성한다.")
    void topic_voteCount_should_noticed() {
        // given
        Topic topic = TestEntityUtil.TestTopic.builder()
                .id(1L)
                .voteCount(voteCountUnit)
                .build().buildTopic();

        // when
        boolean shouldNotice = notificationService.shouldNoticeVoteCountForTopic(topic);

        // then
        assertThat(shouldNotice).isTrue();
    }

    @Test
    @DisplayName("VoteCountOnTopic 알림 생성한다.")
    void create_VoteCountOnTopicNotification() {
        // given
        Member author = createRandomMember();
        em.persist(author);

        Topic topic = TestEntityUtil.TestTopic.builder()
                .author(author)
                .voteCount(voteCountUnit)
                .build().buildTopic();
        em.persist(topic);

        notificationService.noticeVoteCountOnTopic(topic);

        // when
        List<NotificationResponse> responses = notificationService.findAllByReceiverId(author.getId());

        // then
        assertThat(responses.get(0).getMessage()).isInstanceOf(VoteCountOnTopicNotificationMessage.class);
    }
}
