package life.offonoff.ab.application.service.vote.criteria;

import life.offonoff.ab.application.service.vote.votingtopic.container.VotingTopic;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class DeadlineVotingEndCriteria implements VotingEndCriteria {

    private final LocalDateTime compareTime;

    /**
     * 호출 시각 기준으로 VotingTopic의 deadline과 비교
     */
    @Override
    public boolean check(VotingTopic votingTopic) {
        return votingTopic.deadlinePassed(compareTime);
    }
}
