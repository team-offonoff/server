package life.offonoff.ab.application.service.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.TopicStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TopicSearchRequest {

    private TopicStatus status;
    private TopicSide side;
    @JsonProperty(value = "keyword_id")
    private Long keywordId;
}
