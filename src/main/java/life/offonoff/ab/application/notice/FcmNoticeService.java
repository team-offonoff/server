package life.offonoff.ab.application.notice;

import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.application.event.topic.NoticedEvent;
import life.offonoff.ab.application.event.topic.VotingResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FcmNoticeService implements NoticeService {

    private final MemberRepository memberRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void noticeVotingResult(VotingResult result) {
        List<Member> members = memberRepository.findAllVotedTo(result.topicId());

        for (Member member : members) {
            log.info("# Notification send / Topic(id = {}, total_vote_count = {}) Member(id = {})",
                    result.topicId(), result.totalVoteCount(), member.getId());
        }

        // 공지완료 이벤트 발행
        eventPublisher.publishEvent(new NoticedEvent(result.topicId()));
    }
}
