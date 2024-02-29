package life.offonoff.ab.application.notification;

import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.comment.LikedComment;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.notification.CommentOnTopicNotification;
import life.offonoff.ab.domain.notification.LikeInCommentNotification;
import life.offonoff.ab.domain.notification.VoteCountOnTopicNotification;
import life.offonoff.ab.domain.notification.VoteResultNotification;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.repository.notfication.NotificationRepository;
import life.offonoff.ab.web.response.notification.NotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NotificationService {

    @Value("${ab.notification.vote_on_topic.count_unit}")
    public int voteCountUnit;

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void notifyVoteResult(Topic topic) {
        // voters' notifications
        List<Member> voters = memberRepository.findAllListeningVoteResultAndVotedTopicId(topic.getId());
        List<VoteResultNotification> notifications = createVotersNotifications(topic, voters);

        // author's notification
        addAuthorsNotificationIfAuthorListeningVoteResult(topic, notifications);

        notificationRepository.saveVoteResultNotificationsInBatch(notifications);
    }

    private List<VoteResultNotification> createVotersNotifications(Topic topic, List<Member> voters) {
        return voters.stream()
                .filter(Member::listenVoteResult)
                .map(receiver -> {
                            log.info("# Notification send / Topic(id = {}, total_vote_count = {}) Member(id = {})",
                                    topic.getId(), topic.getVoteCount(), receiver.getId());
                            return new VoteResultNotification(receiver, topic);
                        }
                ).collect(Collectors.toList());
    }

    private void addAuthorsNotificationIfAuthorListeningVoteResult(Topic topic, List<VoteResultNotification> notifications) {
        Member author = topic.getAuthor();

        if (author.listenVoteResult()) {
            VoteResultNotification authorsNotification = new VoteResultNotification(author, topic);
            notifications.add(authorsNotification);
        }
    }

    @Transactional
    public void notifyLikeInComment(LikedComment likedComment) {
        if (shouldNotifyLikeInComment(likedComment)) {
            LikeInCommentNotification notification = new LikeInCommentNotification(likedComment.getComment());

            notificationRepository.save(notification);
        }
    }

    private boolean shouldNotifyLikeInComment(LikedComment likedComment) {
        Member liker = likedComment.getLiker();

        Comment comment = likedComment.getComment();
        Member writer = comment.getWriter();

        boolean likerIsWriter = comment.isWrittenBy(liker);
        boolean writerListenLikeInComment = writer.listenLikeInComment();

        return !likerIsWriter && writerListenLikeInComment;
    }

    @Transactional
    public void notifyCommentOnTopic(Comment comment) {
        if (shouldNotifyCommentOnTopic(comment)) {
            CommentOnTopicNotification notification = new CommentOnTopicNotification(comment);

            notificationRepository.save(notification);
        }
    }

    private boolean shouldNotifyCommentOnTopic(Comment comment) {
        Member commenter = comment.getWriter();
        Topic topic = comment.getTopic();

        boolean commenterIsNotAuthor = !topic.isWrittenBy(commenter);
        boolean authorListenCommentOnTopic = topic.getAuthor()
                                                  .listenCommentOnTopic();

        return commenterIsNotAuthor && authorListenCommentOnTopic;
    }

    @Transactional
    public void notifyVoteCountOnTopic(Topic topic) {
        if (shouldNotifyVoteCountForTopic(topic)) {
            VoteCountOnTopicNotification notification = new VoteCountOnTopicNotification(topic);

            notificationRepository.save(notification);
        }
    }

    private boolean shouldNotifyVoteCountForTopic(Topic topic) {
        // TODO : 투표 취소 후 다시 100단위를 넘었을 때 중복 알림 처리 && 추상화
        boolean voteCountDividedByUnit = (topic.getVoteCount() % voteCountUnit) == 0;
        boolean authorListenVoteCountOnTopic = topic.getAuthor()
                                                    .listenVoteCountOnTopic();

        return voteCountDividedByUnit && authorListenVoteCountOnTopic;
    }

    public List<NotificationResponse> findAllByReceiverId(Long memberId) {

        return notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(memberId)
                                     .stream()
                                     .map(NotificationResponse::new)
                                     .toList();
    }

    public Integer countUncheckedByReceiverId(Long memberId) {
        return notificationRepository.countByCheckedFalseAndReceiverId(memberId);
    }
}

