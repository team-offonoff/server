package life.offonoff.ab.application.service.vote.criteria;

import life.offonoff.ab.application.service.vote.votingtopic.container.VotingTopic;

/**
 * 투표 종료의 기준 추상화
 */
@FunctionalInterface
public interface VotingEndCriteria {

    boolean check(VotingTopic votingTopic);
}