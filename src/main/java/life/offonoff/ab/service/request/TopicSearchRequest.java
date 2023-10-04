package life.offonoff.ab.service.request;

import life.offonoff.ab.domain.topic.TopicStatus;
import lombok.Getter;
import lombok.Setter;

import static life.offonoff.ab.repository.specification.TopicSpecificationFactory.*;

@Getter
@Setter
public class TopicSearchRequest implements SpecificationRequest {

    private TopicStatus topicStatus;
    private Long memberId;
    private Boolean hidden;
    private Long categoryId;
}
