package life.offonoff.ab.application.schedule.topic.criteria;

import life.offonoff.ab.application.schedule.topic.VotingTopic;

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
