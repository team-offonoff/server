package life.offonoff.ab.application.service.event.topic;

import life.offonoff.ab.application.event.topic.*;
import life.offonoff.ab.application.notice.NoticeService;
import life.offonoff.ab.application.service.vote.votingtopic.container.VotingTopicContainer;
import life.offonoff.ab.config.vote.ContainerVotingTopicConfig;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.vote.VotingResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class TopicEventHandlerTest {

    @Autowired
    private TopicEventHandler topicEventHandler;
    @MockBean
    private NoticeService noticeService;

    @Test
    @DisplayName("Voting End 시에 Notice Service 호출")
    void invoke_noticeService_when_voting_ended() {
        // given
        Topic topic = TestTopic.builder()
                .id(1L)
                .build().buildTopic();
        VotingResult result = new VotingResult();
        result.setTopic(topic);

        doNothing().when(noticeService).noticeVotingResult(any(VotingResult.class));

        // when
        topicEventHandler.votingEnded(new VotingEndEvent(topic, result));

        // then
        verify(noticeService).noticeVotingResult(any(VotingResult.class));
    }
}