package life.offonoff.ab.application.schedule;

import life.offonoff.ab.application.schedule.topic.criteria.VotingEndCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class GlobalScheduler {

    private final VotingTopicManager votingTopicManager;
    private final VotingEndCriteria criteria;

    /**
     * 매 분마다 스케쥴링
     */
    @Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void checkVotingTopic() {
        log.info("[checkVotingTopic] schedule start");

        votingTopicManager.endVoting(criteria);

        log.info("[checkVotingTopic] schedule ended");
    }
}
