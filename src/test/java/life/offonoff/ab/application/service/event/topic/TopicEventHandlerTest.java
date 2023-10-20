package life.offonoff.ab.application.service.event.topic;

import life.offonoff.ab.application.event.topic.*;
import life.offonoff.ab.application.notice.NoticeService;
import life.offonoff.ab.application.service.vote.votingtopic.VotingTopicContainer;
import life.offonoff.ab.domain.TestEntityUtil;
import life.offonoff.ab.domain.vote.VotingResult;
import life.offonoff.ab.repository.topic.TopicRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class TopicEventHandlerTest {

    @Autowired
    private TopicEventHandler topicEventHandler;
    @Autowired
    private VotingTopicContainer container;
    @MockBean
    private NoticeService noticeService;

    @Test
    @DisplayName("Topic 추가하면 container 사이즈 증가")
    void add_voting_topic() {
        // given
        TopicCreateEvent createEvent = new TopicCreateEvent(1L, LocalDateTime.now());
        topicEventHandler.addTopic(createEvent);

        // when
        int size = container.size();

        // then
        assertThat(size).isEqualTo(1);
    }

    @Test
    @DisplayName("Voting End 시에 Notice Service 호출")
    void invoke_noticeService_when_voting_ended() {
        // given
        Long endedTopicId = 1L;
        VotingResult result = new VotingResult();
        result.setTopic(TestTopic.builder()
                .id(1L)
                .build().buildTopic());

        doNothing().when(noticeService).noticeVotingResult(any(VotingResult.class));

        // when
        topicEventHandler.votingEnded(new VotingEndEvent(endedTopicId, result));

        // then
        verify(noticeService).noticeVotingResult(any(VotingResult.class));
    }
}