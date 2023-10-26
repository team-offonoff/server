package life.offonoff.ab.application.notice;

import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.notice.Notification;
import life.offonoff.ab.domain.notice.VotingResultNotification;
import life.offonoff.ab.domain.vote.VotingResult;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.application.event.topic.NoticedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FcmNoticeService implements NoticeService {

    private final MemberRepository memberRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public void noticeVotingResult(VotingResult result) {
        List<Member> members = memberRepository.findAllVotedTo(result.getTopicId());

        for (Member member : members) {
            log.info("# Notification send / Topic(id = {}, total_vote_count = {}) Member(id = {})",
                    result.getTopicId(), result.getTotalVoteCount(), member.getId());

            /*
               공지 로직
             */

            // 공지 저장
            // TODO 이 부분은 bulk insert로 수정 예정
            Notification notification = new VotingResultNotification(result);
            notification.setMember(member);
        }

        // 공지완료 이벤트 발행
        eventPublisher.publishEvent(new NoticedEvent(result.getTopicId()));
    }
}
