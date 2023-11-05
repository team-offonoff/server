package life.offonoff.ab.config.vote;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ReadableVotingTopicConfig.class)
public class VotingConfig {
}
