package life.offonoff.ab.application.service.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import life.offonoff.ab.domain.topic.TopicStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopicSearchRequest {

    private TopicStatus topicStatus;
    @JsonProperty(value = "keyword_id")
    private Long keywordId;

    @Builder
    public TopicSearchRequest(TopicStatus topicStatus, Long keywordId) {
        this.topicStatus = topicStatus;
        this.keywordId = keywordId;
    }
}
