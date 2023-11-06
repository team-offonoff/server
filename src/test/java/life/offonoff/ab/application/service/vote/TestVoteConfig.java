package life.offonoff.ab.application.service.vote;

import life.offonoff.ab.application.service.vote.votingtopic.container.VotingTopic;
import life.offonoff.ab.application.service.vote.votingtopic.container.VotingTopicContainer;
import life.offonoff.ab.application.service.vote.votingtopic.container.VotingTopicContainerService;
import life.offonoff.ab.application.service.vote.votingtopic.container.store.VotingTopicDeadlineQueue;
import life.offonoff.ab.application.service.vote.votingtopic.container.store.VotingTopicStorage;
import life.offonoff.ab.repository.topic.TopicRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.Comparator;

public class TestVoteConfig {

    @TestConfiguration
    public static class TestReadableVotingTopicConfig {
        @Bean
        @Primary
        public VotingTopicService votingTopicReadableService(
                TopicRepository topicRepository,
                ApplicationEventPublisher eventPublisher
        ) {
            return new VotingTopicReadableService(topicRepository, eventPublisher);
        }
    }

    @TestConfiguration
    public static class TestContainerVotingTopicConfig {
        @Bean
        public Comparator<VotingTopic> votingTopicComparator() {
            return (sch1, sch2) -> sch1.getDeadline().compareTo(sch2.getDeadline());
        }

        @Bean
        public VotingTopicStorage topicScheduleStorage(Comparator<VotingTopic> votingTopicComparator) {
            return new VotingTopicDeadlineQueue(votingTopicComparator);
        }

        @Bean
        public VotingTopicContainer votingTopicContainer(VotingTopicStorage votingTopicStorage) {
            return new VotingTopicContainer(votingTopicStorage);
        }

        @Bean
        @Primary
        public VotingTopicService votingTopicContainerService(
                TopicRepository topicRepository,
                VotingTopicContainer votingTopicContainer,
                ApplicationEventPublisher eventPublisher
        ) {
            return new VotingTopicContainerService(topicRepository, votingTopicContainer, eventPublisher);
        }
    }
}
