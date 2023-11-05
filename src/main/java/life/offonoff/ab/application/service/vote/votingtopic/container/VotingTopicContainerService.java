package life.offonoff.ab.application.service.vote.votingtopic.container;

import life.offonoff.ab.application.event.topic.VotingEndEvent;
import life.offonoff.ab.application.service.vote.VotingTopicService;
import life.offonoff.ab.application.service.vote.criteria.VotingEndCriteria;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.domain.vote.VotingResult;
import life.offonoff.ab.exception.TopicNotFoundException;
import life.offonoff.ab.repository.topic.TopicRepository;
import life.offonoff.ab.repository.topic.TopicSearchCond;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VotingTopicContainerService implements VotingTopicService {

    private final TopicRepository topicRepository;

    private final VotingTopicContainer container;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener(ApplicationReadyEvent.class)
    public void resumeVoting() {
        List<VotingTopic> votingTopics
                = topicRepository.findAll(new TopicSearchCond(LocalDateTime.now(), null, TopicStatus.VOTING))
                                 .stream()
                                 .map(VotingTopic::new)
                                 .toList();

        container.load(votingTopics);
    }

    @Override
    public void startVote(VotingTopic votingTopic) {
        container.insert(votingTopic);
    }

    /**
     * time 기준 deadline이 지난 topic을 container에서 찾아 status를 바꿈
     */
    @Transactional
    @Override
    public void endVote(VotingEndCriteria criteria) {
        List<VotingTopic> ended = container.getVotingEnded(criteria);
        log.info("Voting Ended : {}", ended.size());
        ended.forEach(vt -> {
            Topic topic = vt.getTopic();
            topic.endVote();

            VotingResult result = aggregateVote(topic);

            // 투표 종료 이벤트 발행
            eventPublisher.publishEvent(new VotingEndEvent(topic, result));
        });
    }
}
