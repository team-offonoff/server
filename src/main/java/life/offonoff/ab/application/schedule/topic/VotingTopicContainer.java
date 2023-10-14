package life.offonoff.ab.application.schedule.topic;

import jakarta.annotation.PostConstruct;
import life.offonoff.ab.application.schedule.topic.criteria.VotingEndCriteria;
import life.offonoff.ab.repository.topic.TopicRepository;
import life.offonoff.ab.application.schedule.topic.storage.VotingTopicStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class VotingTopicContainer {

    private final VotingTopicStorage storage;
    private final TopicRepository topicRepository;

    /**
     * 스프링 컨테이너 재가동시 Voting 중인 Topic을 다시 load
     *
     * TODO Voting Topic 관리를 분리할 필요가 있음 (예 redis)
     */
    @PostConstruct
    public void init() {
        this.storage.loadInVoting(topicRepository.findAllInVoting(LocalDateTime.now()));
    }

    public void insert(VotingTopic votingTopic) {
        remove(votingTopic);
        storage.add(votingTopic);
        log.info("new TopicSchedules({}) added, total schedules : {}", votingTopic, storage.size());
    }

    public void remove(VotingTopic votingTopic) {
        storage.remove(votingTopic);
    }

    public int size() {
        return storage.size();
    }

    /**
     * 투표가 끝난 토픽 반환
     */
    public List<VotingTopic> getVotingEnded(VotingEndCriteria criteria) {
        return storage.popAllIf(criteria::check);
    }
}
