package life.offonoff.ab.config;

import life.offonoff.ab.application.schedule.topic.VotingTopic;
import life.offonoff.ab.application.schedule.topic.criteria.DeadlineVotingEndCriteria;
import life.offonoff.ab.application.schedule.topic.criteria.VotingEndCriteria;
import life.offonoff.ab.application.schedule.topic.storage.VotingTopicDeadlineQueue;
import life.offonoff.ab.application.schedule.topic.storage.VotingTopicStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Comparator;

@EnableScheduling
@Configuration
public class ScheduleConfig {

    @Bean(name = "votingTopicComparator")
    public Comparator<VotingTopic> votingTopicComparator() {
        return (sch1, sch2) -> sch1.deadline().compareTo(sch2.deadline());
    }

    @Bean
    public VotingTopicStorage topicScheduleStorage(Comparator<VotingTopic> votingTopicComparator) {
        return new VotingTopicDeadlineQueue(votingTopicComparator);
    }

    @Bean
    public VotingEndCriteria votingEndCriteria() {
        return new DeadlineVotingEndCriteria();
    }
}
