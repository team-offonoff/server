package life.offonoff.ab.application.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class GlobalScheduler {

    private final VotingManager votingManager;

    /**
     * 매 분마다 스케쥴링
     */
    @Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void checkVotingTopic() {
        log.info("[checkVotingTopic] schedule start");

        // 분 단위
        LocalDateTime timeInMinute = LocalDateTime.now()
                .withSecond(0)
                .withNano(0);

        votingManager.endVoting(timeInMinute);

        log.info("[checkVotingTopic] schedule ended");
    }
}
