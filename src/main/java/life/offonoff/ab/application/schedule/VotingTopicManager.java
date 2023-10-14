package life.offonoff.ab.application.schedule;

import life.offonoff.ab.application.event.topic.VotingEndEvent;
import life.offonoff.ab.application.schedule.topic.VotingTopicContainer;
import life.offonoff.ab.application.schedule.topic.criteria.VotingEndCriteria;
import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.repository.topic.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class VotingTopicManager {

    private final VotingTopicContainer container;
    private final TopicRepository topicRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * time 기준 deadline이 지난 topic을 container에서 찾아 status를 바꿈
     */
    public void endVoting(VotingEndCriteria criteria) {
        container.getVotingEnded(criteria)
                .forEach(vt -> {
                            topicRepository.updateStatus(vt.topicId(), TopicStatus.VOTING_ENDED);
                            // 투표 종료 이벤트 발행
                            eventPublisher.publishEvent(new VotingEndEvent(vt.topicId()));
                        }
                );
    }
}
