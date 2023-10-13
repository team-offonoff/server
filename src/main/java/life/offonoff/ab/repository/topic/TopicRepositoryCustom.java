package life.offonoff.ab.repository.topic;

import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.application.event.topic.VotingResult;
import life.offonoff.ab.application.service.request.TopicSearchRequest;
import life.offonoff.ab.application.schedule.topic.VotingTopic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface TopicRepositoryCustom {

    Slice<Topic> findAll(TopicSearchRequest request, Pageable pageable);

    VotingResult findVotingResultById(Long topicId);

    List<VotingTopic> findAllInVoting(LocalDateTime time);

    @Transactional
    void updateStatus(Long topicId, TopicStatus topicStatus);
}
