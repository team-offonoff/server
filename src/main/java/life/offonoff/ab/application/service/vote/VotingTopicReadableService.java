package life.offonoff.ab.application.service.vote;

import life.offonoff.ab.application.event.topic.VoteClosedEvent;
import life.offonoff.ab.application.service.vote.criteria.DeadlineVoteClosingCriteria;
import life.offonoff.ab.application.service.vote.criteria.VoteClosingCriteria;
import life.offonoff.ab.application.service.vote.votingtopic.container.VotingTopic;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.repository.topic.TopicRepository;
import life.offonoff.ab.repository.topic.TopicSearchCond;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VotingTopicReadableService implements VotingTopicService {

    private final TopicRepository topicRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void startVote(VotingTopic votingTopic) {
        // Do Nothing
    }

    /**
     * time 기준 deadline이 지난 topic을 container에서 찾아 status를 바꿈
     */
    @Transactional
    @Override
    public void endVote(VoteClosingCriteria criteria) {
        List<Topic> ended = topicRepository.findAll(createTopicSearchCond(criteria));
        log.info("Voting Ended : {}", ended.size());
        ended.forEach(
                topic -> {
                    topic.closeVote();

                    // 투표 종료 이벤트 발행
                    eventPublisher.publishEvent(new VoteClosedEvent(topic));
                }
        );
    }

    private TopicSearchCond createTopicSearchCond(VoteClosingCriteria criteria) {

        if (criteria instanceof DeadlineVoteClosingCriteria deadlineCriteria) {
            return new TopicSearchCond(null, deadlineCriteria.getCompareTime(), TopicStatus.VOTING);
        }
        return null;
    }
}
