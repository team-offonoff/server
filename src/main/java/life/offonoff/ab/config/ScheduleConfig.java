package life.offonoff.ab.config;

import life.offonoff.ab.service.schedule.topic.storage.VotingTopicQueue;
import life.offonoff.ab.service.schedule.topic.storage.VotingTopicStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@Configuration
public class ScheduleConfig {

    @Bean
    public VotingTopicStorage topicScheduleStorage() {
        return new VotingTopicQueue(
                (sch1, sch2) -> sch1.deadline().compareTo(sch2.deadline())
        );
    }
}
