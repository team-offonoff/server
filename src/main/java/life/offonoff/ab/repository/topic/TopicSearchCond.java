package life.offonoff.ab.repository.topic;

import life.offonoff.ab.domain.topic.TopicStatus;

import java.time.LocalDateTime;

public record TopicSearchCond(
        LocalDateTime startCompareTime,
        LocalDateTime endCompareTime,
        TopicStatus topicStatus
) {

}
