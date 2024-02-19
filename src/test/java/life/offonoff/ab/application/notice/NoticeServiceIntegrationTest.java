package life.offonoff.ab.application.notice;

import jakarta.persistence.EntityManager;
import life.offonoff.ab.domain.TestEntityUtil;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.web.response.notice.NoticeResponse;
import life.offonoff.ab.web.response.notice.VoteCountOnTopicNoticeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static life.offonoff.ab.application.notice.NoticeService.VOTE_COUNT_MODULO;
import static life.offonoff.ab.domain.TestEntityUtil.createRandomMember;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@DisplayName("NoticeService 스프링 컨테이너 통합테스트")
public class NoticeServiceIntegrationTest {

    @Autowired
    NoticeService noticeService;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("VoteCountOnTopic 알림 생성한다.")
    void create_VoteCountOnTopicNotification() {
        // given
        Member author = createRandomMember();
        em.persist(author);

        Topic topic = TestEntityUtil.TestTopic.builder()
                .author(author)
                .voteCount(VOTE_COUNT_MODULO)
                .build().buildTopic();
        em.persist(topic);

        noticeService.noticeVoteCountOnTopic(topic);

        // when
        List<NoticeResponse> responses = noticeService.findAllByReceiverId(author.getId());

        // then
        assertThat(responses.get(0)).isInstanceOf(VoteCountOnTopicNoticeResponse.class);
    }
}
