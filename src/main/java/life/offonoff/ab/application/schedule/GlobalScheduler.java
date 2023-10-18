package life.offonoff.ab.application.schedule;

import life.offonoff.ab.application.service.vote.VotingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class GlobalScheduler {

    private final VotingService votingService;

    /**
     * 매 분마다 스케쥴링
     */
    @Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void checkVotingDeadline() {
        log.info("[checkVotingTopic] schedule start");

        votingService.endVoting(
                votingTopic -> votingTopic.deadlinePassed(getStandardLocalDateTime())
        );

        log.info("[checkVotingTopic] schedule ended");
    }

    private LocalDateTime getStandardLocalDateTime() {
        return LocalDateTime.now()
                        .withSecond(0)
                        .withNano(0);
    }
}
