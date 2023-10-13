package life.offonoff.ab.application.event.topic;

import life.offonoff.ab.application.schedule.topic.VotingTopic;
import life.offonoff.ab.application.schedule.topic.VotingTopicContainer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class TopicEventHandler {

    private final VotingTopicContainer votingTopicContainer;

    /**
     * 투표 생성 이벤트 -> VotingTopicContainer에서 관리 추가
     */
    @TransactionalEventListener
    public void addTopic(TopicCreateEvent event) {
        log.info("# Topic created / topic-id : {}, deadline : {}", event.topicId(), event.deadline());
        votingTopicContainer.insert(new VotingTopic(event.topicId(), event.deadline()));
    }
}
