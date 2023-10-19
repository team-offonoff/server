package life.offonoff.ab.application.event.topic;

import life.offonoff.ab.application.service.vote.votingtopic.VotingTopic;
import life.offonoff.ab.application.service.vote.votingtopic.VotingTopicContainer;
import life.offonoff.ab.application.notice.NoticeService;
import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.repository.topic.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    @EventListener
    public void addTopic(TopicCreateEvent event) {
        log.info("# Topic Created / topic-id : {}, deadline : {}", event.topicId(), event.deadline());
        votingTopicContainer.insert(new VotingTopic(event.topicId(), event.deadline()));
    }

    /**
     * 투표 종료 이벤트 -> 투표 결과 만들어서 NoticeService로 공지
     */
    @EventListener
    public void votingEnded(VotingEndEvent event) {
        log.info("# Topic Voting Ended / topic-id : {}", event.topicId());

        noticeService.noticeVotingResult(event.result());
    }

    /**
     * 투표 결과 전송 -> 투표 공지 status 수정
     */
    @Transactional
    @EventListener
    public void noticed(NoticedEvent event) {
        log.info("# Topic Noticed / topic-id : {}", event.topicId());
        topicRepository.updateStatus(event.topicId(), TopicStatus.NOTICED);
    }
}
