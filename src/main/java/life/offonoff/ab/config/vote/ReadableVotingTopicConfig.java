package life.offonoff.ab.config.vote;

import life.offonoff.ab.application.service.vote.VotingTopicReadableService;
import life.offonoff.ab.application.service.vote.VotingTopicService;
import life.offonoff.ab.repository.topic.TopicRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;

@Slf4j
public class ReadableVotingTopicConfig {

    @Bean
    public VotingTopicService votingTopicReadableService(
            TopicRepository topicRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        log.info("# VotingTopicService, class : {}", VotingTopicReadableService.class);
        return new VotingTopicReadableService(topicRepository, eventPublisher);
    }
}