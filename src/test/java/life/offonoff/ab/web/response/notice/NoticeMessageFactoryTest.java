package life.offonoff.ab.web.response.notice;

import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.notice.VoteCountOnTopicNotification;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.web.response.notice.message.NoticeMessage;
import life.offonoff.ab.web.response.notice.message.NoticeMessageFactory;
import life.offonoff.ab.web.response.notice.message.VoteCountOnTopicNoticeMessage;
import org.junit.jupiter.api.Test;

import static life.offonoff.ab.domain.TestEntityUtil.createRandomMember;
import static life.offonoff.ab.domain.TestEntityUtil.createRandomTopic;
import static org.assertj.core.api.Assertions.*;

class NoticeMessageFactoryTest {

    @Test
    void create_NoticeMessage() {
        // given
        Topic topic = createRandomTopic();
        Member member = createRandomMember();

        VoteCountOnTopicNotification notification = new VoteCountOnTopicNotification(member, topic);

        // when
        NoticeMessage noticeMessage = NoticeMessageFactory.createNoticeMessage(notification);

        // then
        assertThat(noticeMessage).isInstanceOf(VoteCountOnTopicNoticeMessage.class);
    }
}