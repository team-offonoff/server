package life.offonoff.ab.application.service.event.topic;

import life.offonoff.ab.application.event.topic.*;
import life.offonoff.ab.application.notification.NotificationService;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.domain.vote.Vote;
import life.offonoff.ab.domain.vote.VoteResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class TopicEventHandlerTest {

    @Autowired
    private TopicEventHandler topicEventHandler;
    @MockBean
    private NotificationService notificationService;

    @Test
    @DisplayName("Voting End 시에 Notice Service 호출")
    void invoke_noticeService_when_voting_ended() {
        // given
        Topic topic = TestTopic.builder()
                .id(1L)
                .build().buildTopic();
        VoteResult result = new VoteResult();
        result.setTopic(topic);

        doNothing().when(notificationService).noticeVoteResult(any(VoteResult.class));

        // when
        topicEventHandler.voteClosed(new VoteClosedEvent(topic, result));

        // then
        verify(notificationService).noticeVoteResult(any(VoteResult.class));
    }

    @Test
    @DisplayName("VoteCount 알림 기준에 해당되면 알림 생성 메서드 호출한다.")
    void noticeVoteCount_when_shouldNoticeVoteCount() {
        // given
        Vote vote = createVote(ChoiceOption.CHOICE_A);

        when(notificationService.shouldNoticeVoteCountForTopic(any()))
                .thenReturn(true);

        // when
        topicEventHandler.voted(new VotedEvent(vote));

        // then
        verify(notificationService).noticeVoteCountOnTopic(any());
    }
}