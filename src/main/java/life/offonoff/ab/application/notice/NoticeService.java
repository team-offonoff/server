package life.offonoff.ab.application.notice;

import life.offonoff.ab.domain.vote.VoteResult;

public interface NoticeService {

    void noticeVotingResult(VoteResult result);
}
