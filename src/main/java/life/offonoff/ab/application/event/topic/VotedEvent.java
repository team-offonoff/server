package life.offonoff.ab.application.event.topic;

import life.offonoff.ab.domain.vote.Vote;
import lombok.Getter;

@Getter
public class VotedEvent {

    private Vote vote;

    public VotedEvent(Vote vote) {
        this.vote = vote;
    }
}
