package life.offonoff.ab.application.event.topic;

import life.offonoff.ab.application.notification.NotificationService;
import life.offonoff.ab.application.service.vote.VotingTopicService;
import life.offonoff.ab.application.service.vote.votingtopic.container.VotingTopic;
import life.offonoff.ab.domain.topic.Topic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 토픽 생성 -> 투표 중 -> 투표 끝 -> 결과 집계 -> 결과 공지
 *
 * 트랜잭션 분리 필요
 *
 * 1. 토픽 생성 -> [투표 중 (이벤트) -> ]
 * 2. 투표 끝 -> 결과 집계 -> [결과 공지 (이벤트) -> 토픽 noticed]
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class TopicEventHandler {

    private final VotingTopicService votingTopicService;
    private final NotificationService notificationService;

    /**
     * 투표 생성 이벤트 -> VotingTopicContainer에서 관리 추가
     */
    @EventListener
    public void addTopic(TopicCreateEvent event) {
        log.info("# Topic Created / topic-id : {}, deadline : {}", event.topic().getId(), event.topic().getDeadline());

        votingTopicService.startVote(new VotingTopic(event.topic()));
    }

    /**
     * 투표 종료 이벤트 -> 투표 결과 만들어서 NoticeService로 공지
     */
    @EventListener
    public void voteClosed(VoteClosedEvent event) {
        log.info("# Topic Vote Closed / topic-id : {}, deadline : {}", event.topic().getId(), event.topic().getDeadline());

        notificationService.notifyVoteResult(event.result());
    }

    /**
     * 투표 이벤트
     */
    @EventListener
    public void voted(VotedEvent event) {
        Topic topic = event.getVote()
                           .getTopic();

        notificationService.notifyVoteCountOnTopic(topic);
    }

    /**
     * 댓글 이벤트
     */
    @EventListener
    public void commented(CommentedEvent event) {
        notificationService.notifyCommentOnTopic(event.getComment());
    }
}
