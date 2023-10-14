package life.offonoff.ab.application.notice;

import life.offonoff.ab.application.event.topic.VotingResult;

public interface NoticeService {

    void noticeVotingResult(VotingResult result);
}
