package life.offonoff.ab.service.event.topic;

import life.offonoff.ab.application.event.topic.*;
import life.offonoff.ab.application.notice.NoticeService;
import life.offonoff.ab.application.schedule.topic.VotingTopicContainer;
import life.offonoff.ab.repository.topic.TopicRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class TopicEventHandlerTest {

    @Autowired
    private TopicEventHandler topicEventHandler;
    @Autowired
    private VotingTopicContainer container;
    @MockBean
    private TopicRepository topicRepository;
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
        VotingResult result = createVotingResult(endedTopicId);

        when(topicRepository.findVotingResultById(anyLong())).thenReturn(result);
        doNothing().when(noticeService).noticeVotingResult(any(VotingResult.class));

        // when
        topicEventHandler.votingEnded(new VotingEndEvent(endedTopicId));

        // then
        verify(noticeService).noticeVotingResult(any(VotingResult.class));
    }

    private VotingResult createVotingResult(Long topicId) {
        return new VotingResult(
                topicId,
                "title",
                "category",
                "memberName",
                0
        );
    }
}