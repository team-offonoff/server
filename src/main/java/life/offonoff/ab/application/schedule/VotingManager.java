package life.offonoff.ab.application.schedule;

import life.offonoff.ab.application.event.topic.VotingEndEvent;
import life.offonoff.ab.application.schedule.topic.VotingTopicContainer;
import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.repository.topic.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class VotingManager {

    private final VotingTopicContainer container;
    private final ApplicationEventPublisher eventPublisher;
    private final TopicRepository topicRepository;

    /**
     * time 기준 deadline이 지난 topic을 container에서 찾아 status를 바꿈
     */
    public void endVoting(LocalDateTime time) {
        container.getVotingEnded(time)
                .forEach(vt -> {
                            topicRepository.updateStatus(vt.topicId(), TopicStatus.VOTING_ENDED);
                            // 투표 종료 이벤트 발행
                            eventPublisher.publishEvent(new VotingEndEvent(vt.topicId()));
                        }
                );
    }
}
