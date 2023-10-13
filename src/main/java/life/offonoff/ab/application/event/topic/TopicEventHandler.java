package life.offonoff.ab.application.event.topic;

import life.offonoff.ab.application.schedule.topic.VotingTopic;
import life.offonoff.ab.application.schedule.topic.VotingTopicContainer;
import life.offonoff.ab.application.notice.NoticeService;
import life.offonoff.ab.repository.topic.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class TopicEventHandler {

    private final VotingTopicContainer votingTopicContainer;
    private final TopicRepository topicRepository;
    private final NoticeService noticeService;

    /**
     * 투표 생성 이벤트 -> VotingTopicContainer에서 관리 추가
     */
    @TransactionalEventListener
    public void addTopic(TopicCreateEvent event) {
        log.info("# Topic created / topic-id : {}, deadline : {}", event.topicId(), event.deadline());
        votingTopicContainer.insert(new VotingTopic(event.topicId(), event.deadline()));
    }

    /**
     * 투표 종료 이벤트 -> 투표 종료 status 수정 + 투표 결과 만들어서 NoticeService로 공지
     */
    @EventListener
    public void votingEnded(VotingEndEvent event) {
        Long topicId = event.topicId();
        log.info("# Voting Ended / topic-id : {}", topicId);

        noticeService.noticeVotingResult(topicRepository.findVotingResultById(topicId));
    }
}
