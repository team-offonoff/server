package life.offonoff.ab.application.event.topic;

import life.offonoff.ab.domain.topic.Topic;

public record NoticedEvent(
        Topic topic
) {
}
