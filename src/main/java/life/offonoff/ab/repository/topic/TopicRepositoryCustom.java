package life.offonoff.ab.repository.topic;

import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.application.service.request.TopicSearchRequest;
import life.offonoff.ab.application.service.vote.votingtopic.VotingTopic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface TopicRepositoryCustom {

    Slice<Topic> findAll(TopicSearchRequest request, Pageable pageable);

    List<VotingTopic> findAll(VotingTopicSearchCond cond);

    @Transactional
    void updateStatus(Long topicId, TopicStatus topicStatus);
}
