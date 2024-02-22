package life.offonoff.ab.web.response.notice;

import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.notice.VoteCountOnTopicNotification;
import life.offonoff.ab.domain.topic.Topic;
import org.junit.jupiter.api.Test;

import static life.offonoff.ab.domain.TestEntityUtil.createRandomMember;
import static life.offonoff.ab.domain.TestEntityUtil.createRandomTopic;
import static org.assertj.core.api.Assertions.*;

class NoticeResponseFactoryTest {

    @Test
    void create_NoticeResponse() {
        // given
        Topic topic = createRandomTopic();
        Member member = createRandomMember();

        VoteCountOnTopicNotification notification = new VoteCountOnTopicNotification(member, topic);

        // when
        NoticeResponse noticeResponse = NoticeResponseFactory.createNoticeResponse(notification);

        // then
        assertThat(noticeResponse).isInstanceOf(VoteCountOnTopicNoticeResponse.class);
    }
}