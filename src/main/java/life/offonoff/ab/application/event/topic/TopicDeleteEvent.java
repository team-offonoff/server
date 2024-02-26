package life.offonoff.ab.application.event.topic;

import life.offonoff.ab.domain.topic.Topic;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TopicDeleteEvent {

    private final Topic topic;

    public TopicDeleteEvent(Topic topic) {
        this.topic = topic;
    }
}
