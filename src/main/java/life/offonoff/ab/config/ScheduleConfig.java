package life.offonoff.ab.config;

import life.offonoff.ab.application.service.vote.votingtopic.VotingTopic;
import life.offonoff.ab.application.service.vote.criteria.DeadlineVotingEndCriteria;
import life.offonoff.ab.application.service.vote.criteria.VotingEndCriteria;
import life.offonoff.ab.application.service.vote.votingtopic.storage.VotingTopicDeadlineQueue;
import life.offonoff.ab.application.service.vote.votingtopic.storage.VotingTopicStorage;
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
