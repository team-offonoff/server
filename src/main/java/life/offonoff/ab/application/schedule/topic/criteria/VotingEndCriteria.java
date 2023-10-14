package life.offonoff.ab.application.schedule.topic.criteria;

import life.offonoff.ab.application.schedule.topic.VotingTopic;

/**
 * 투표 종료의 기준 추상화
 */
@FunctionalInterface
public interface VotingEndCriteria {

    boolean check(VotingTopic votingTopic);
}