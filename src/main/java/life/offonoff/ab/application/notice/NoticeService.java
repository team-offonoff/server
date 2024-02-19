package life.offonoff.ab.application.notice;

import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.notice.VoteCountOnTopicNotification;
import life.offonoff.ab.domain.notice.VoteResultNotification;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.vote.VoteResult;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.repository.notice.NotificationRepository;
import life.offonoff.ab.web.response.notice.NoticeResponse;
import life.offonoff.ab.web.response.notice.NoticeResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class NoticeService {

    public static final int VOTE_COUNT_MODULO = 100;

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
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

    @Transactional
    public void noticeLikeInComment() {

    }

    @Transactional
    public void noticeCommentOnTopic() {

    }

    @Transactional
    public void noticeVoteCountOnTopic(Topic topic) {
        Member author = topic.getAuthor();
        VoteCountOnTopicNotification notification = new VoteCountOnTopicNotification(author, topic);

        notificationRepository.save(notification);
    }

    public List<NoticeResponse> findAllByReceiverId(Long memberId) {

        return notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(memberId)
                                     .stream()
                                     .map(NoticeResponseFactory::createNoticeResponse)
                                     .toList();
    }

    public boolean shouldNoticeVoteCountForTopic(Topic topic) {
        // TODO : 투표 취소 후 다시 100단위를 넘었을 때 중복 알림 처리 && 추상화
        return topic.getVoteCount() % VOTE_COUNT_MODULO == 0;
    }
}

