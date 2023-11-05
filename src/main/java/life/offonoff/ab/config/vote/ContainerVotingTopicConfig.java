package life.offonoff.ab.config.vote;

import life.offonoff.ab.application.service.vote.VotingTopicService;
import life.offonoff.ab.application.service.vote.votingtopic.container.VotingTopic;
import life.offonoff.ab.application.service.vote.votingtopic.container.VotingTopicContainer;
import life.offonoff.ab.application.service.vote.votingtopic.container.VotingTopicContainerService;
import life.offonoff.ab.application.service.vote.votingtopic.container.store.VotingTopicDeadlineQueue;
import life.offonoff.ab.application.service.vote.votingtopic.container.store.VotingTopicStorage;
import life.offonoff.ab.repository.topic.TopicRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;

import java.util.Comparator;

@Slf4j
public class ContainerVotingTopicConfig {

    @Bean
    public Comparator<VotingTopic> votingTopicComparator() {
        return (sch1, sch2) -> sch1.getDeadline().compareTo(sch2.getDeadline());
    }

    @Bean
    public VotingTopicStorage topicScheduleStorage() {
        return new VotingTopicDeadlineQueue(votingTopicComparator());
    }

    @Bean
    public VotingTopicContainer votingTopicContainer() {
        return new VotingTopicContainer(topicScheduleStorage());
    }

    @Bean
    public VotingTopicService votingTopicContainerService(
            TopicRepository topicRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        log.info("# VotingTopicService, class : {}", VotingTopicContainerService.class);
        return new VotingTopicContainerService(topicRepository, votingTopicContainer(), eventPublisher);
    }
}
