package life.offonoff.ab.service.event.topic;

import life.offonoff.ab.application.event.topic.TopicCreateEvent;
import life.offonoff.ab.application.event.topic.TopicEventHandler;
import life.offonoff.ab.application.schedule.topic.VotingTopicContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class TopicEventHandlerTest {

    @Autowired
    private TopicEventHandler topicEventHandler;

    @Autowired
    private VotingTopicContainer container;

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
}