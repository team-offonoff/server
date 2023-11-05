package life.offonoff.ab.application.service.vote.votingtopic.container;

import life.offonoff.ab.domain.topic.Topic;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class VotingTopic {

    private final Topic topic;

    public LocalDateTime getDeadline() {
        return topic.getDeadline();
    }

    public boolean deadlinePassed(LocalDateTime time) {
        return topic.getDeadline()
                    .isBefore(time);
    }
}
