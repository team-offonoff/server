package life.offonoff.ab.application.event.topic;

import life.offonoff.ab.application.service.vote.VotingTopicService;
import life.offonoff.ab.application.service.vote.votingtopic.container.VotingTopic;
import life.offonoff.ab.application.notice.NoticeService;
import life.offonoff.ab.domain.topic.Topic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    private final NoticeService noticeService;

    /**
     * 투표 생성 이벤트 -> VotingTopicContainer에서 관리 추가
     */
    @EventListener
    public void addTopic(TopicCreateEvent event) {
        log.info("# Topic Created / topic-id : {}, deadline : {}", event.topic(), event.topic().getDeadline());

        votingTopicService.startVote(new VotingTopic(event.topic()));
    }

    /**
     * 투표 종료 이벤트 -> 투표 결과 만들어서 NoticeService로 공지
     */
    @EventListener
    public void votingEnded(VotingEndEvent event) {
        log.info("# Topic Voting Ended / topic-id : {}", event.topic());

        noticeService.noticeVotingResult(event.result());
    }

    /**
     * 투표 결과 전송 -> 투표 공지 status 수정
     */
    @Transactional
    @EventListener
    public void noticed(NoticedEvent event) {
        log.info("# Topic Noticed / topic-id : {}", event.topic());

        Topic topic = event.topic();
        topic.noticed();
    }
}
