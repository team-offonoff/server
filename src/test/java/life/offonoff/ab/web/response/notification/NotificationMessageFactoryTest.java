package life.offonoff.ab.web.response.notification;

import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.notification.VoteCountOnTopicNotification;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.web.response.notification.message.NotificationMessage;
import life.offonoff.ab.web.response.notification.message.NotificationMessageFactory;
import life.offonoff.ab.web.response.notification.message.VoteCountOnTopicNotificationMessage;
import org.junit.jupiter.api.Test;

import static life.offonoff.ab.domain.TestEntityUtil.createRandomMember;
import static life.offonoff.ab.domain.TestEntityUtil.createRandomTopic;
import static org.assertj.core.api.Assertions.*;

class NotificationMessageFactoryTest {

    @Test
    void create_NoticeMessage() {
        // given
        Topic topic = createRandomTopic();
        Member member = createRandomMember();

        VoteCountOnTopicNotification notification = new VoteCountOnTopicNotification(member, topic);

        // when
        NotificationMessage notificationMessage = NotificationMessageFactory.createNoticeMessage(notification);

        // then
        assertThat(notificationMessage).isInstanceOf(VoteCountOnTopicNotificationMessage.class);
    }
}