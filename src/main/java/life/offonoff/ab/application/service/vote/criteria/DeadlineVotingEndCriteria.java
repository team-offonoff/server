package life.offonoff.ab.application.service.vote.criteria;

import life.offonoff.ab.application.service.vote.votingtopic.VotingTopic;

import java.time.LocalDateTime;

public class DeadlineVotingEndCriteria implements VotingEndCriteria {

    /**
     * 호출 시각 기준으로 VotingTopic의 deadline과 비교
     */
    @Override
    public boolean check(VotingTopic votingTopic) {
        return votingTopic.deadlinePassed(LocalDateTime.now());
    }
}
