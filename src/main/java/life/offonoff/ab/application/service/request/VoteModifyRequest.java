package life.offonoff.ab.application.service.request;

import life.offonoff.ab.domain.topic.TopicSide;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VoteModifyRequest {

    private TopicSide modifiedTopicSide;
    private Long modifiedAt;
}
