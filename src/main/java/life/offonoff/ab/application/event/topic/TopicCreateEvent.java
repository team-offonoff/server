package life.offonoff.ab.application.event.topic;


import life.offonoff.ab.domain.topic.Topic;

import java.time.LocalDateTime;

public record TopicCreateEvent(
        Long topicId,
        LocalDateTime deadline
) {

    public static TopicCreateEvent of(Topic topic) {
        return new TopicCreateEvent(topic.getId(), topic.getDeadline());
    }
}
