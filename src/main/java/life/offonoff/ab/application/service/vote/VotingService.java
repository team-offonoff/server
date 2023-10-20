package life.offonoff.ab.application.service.vote;

import life.offonoff.ab.application.event.topic.VotingEndEvent;
import life.offonoff.ab.application.service.vote.votingtopic.VotingTopic;
import life.offonoff.ab.application.service.vote.votingtopic.VotingTopicContainer;
import life.offonoff.ab.application.service.vote.criteria.VotingEndCriteria;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.domain.vote.VotingResult;
import life.offonoff.ab.exception.TopicNotFoundException;
import life.offonoff.ab.repository.topic.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Component
public class VotingService {

    private final TopicRepository topicRepository;

    private final VotingTopicContainer container;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener(ApplicationReadyEvent.class)
    public void resumeVoting() {
        container.load(topicRepository.findAllInVoting(LocalDateTime.now()));
    }

    /**
     * time 기준 deadline이 지난 topic을 container에서 찾아 status를 바꿈
     */
    @Transactional
    public void endVoting(VotingEndCriteria criteria) {
        List<VotingTopic> ended = container.getVotingEnded(criteria);
        log.info("Voting Ended : {}", ended.size());
        ended.forEach(vt -> {
            Topic topic = findTopic(vt.topicId());
            topic.endVote();

            VotingResult result = aggregateVote(topic);

            // 투표 종료 이벤트 발행
            eventPublisher.publishEvent(new VotingEndEvent(topic.getId(), result));
        });
    }

    public VotingResult aggregateVote(Topic topic) {
        VotingResult votingResult = new VotingResult();
        votingResult.setTopic(topic);
        return votingResult;
    }

    private Topic findTopic(Long topicId) {
        return topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException(topicId));
    }
}
