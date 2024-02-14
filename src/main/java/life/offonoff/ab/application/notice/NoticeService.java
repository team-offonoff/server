package life.offonoff.ab.application.notice;

import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.notice.VoteResultNotification;
import life.offonoff.ab.domain.vote.VoteResult;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.repository.notice.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class NoticeService {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    public void noticeVoteResult(VoteResult result) {
        // voters' notifications
        List<Member> voters = memberRepository.findAllListeningVoteResultAndVotedTopicId(result.getTopicId());
        List<VoteResultNotification> notifications = createVotersNotifications(result, voters);

        // author's notification
        addAuthorsNotificationIfAuthorListeningVoteResult(result, notifications);

        notificationRepository.saveAll(notifications);
    }

    private List<VoteResultNotification> createVotersNotifications(VoteResult result, List<Member> voters) {
        return voters.stream()
                .map(receiver -> {
                            log.info("# Notification send / Topic(id = {}, total_vote_count = {}) Member(id = {})",
                                    result.getTopicId(), result.getTotalVoteCount(), receiver.getId());
                            return new VoteResultNotification(receiver, result);
                        }
                ).collect(Collectors.toList());
    }

    private void addAuthorsNotificationIfAuthorListeningVoteResult(VoteResult result, List<VoteResultNotification> notifications) {
        Member author = result.getTopic()
                              .getAuthor();

        if (author.listenVoteResult()) {
            VoteResultNotification authorsNotification = new VoteResultNotification(result.getTopic().getAuthor(), result);
            notifications.add(authorsNotification);
        }
    }

    public void noticeLikeInComment() {

    }

    public void noticeCommentOnTopic() {

    }

    public void noticeVoteCountOnTopic() {

    }
}

