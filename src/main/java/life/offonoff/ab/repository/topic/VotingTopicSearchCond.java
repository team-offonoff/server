package life.offonoff.ab.repository.topic;

import life.offonoff.ab.domain.topic.TopicStatus;

import java.time.LocalDateTime;

public record VotingTopicSearchCond(
        LocalDateTime compareTime,
        TopicStatus topicStatus
) {

}
