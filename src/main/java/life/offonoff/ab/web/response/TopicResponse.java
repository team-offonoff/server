package life.offonoff.ab.web.response;

import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import lombok.Builder;

import java.util.List;

@Builder
public record TopicResponse(
        Long topicId,
        TopicSide topicSide,
        String topicTitle,
        List<KeywordResponse> keywords,
        List<ChoiceResponse> choices
) {
    public static TopicResponse from(Topic topic) {
        return new TopicResponse(
                topic.getId(),
                topic.getSide(),
                topic.getTitle(),
                topic.getTopicKeywords().stream()
                        .map(topicKeyword -> KeywordResponse.from(topicKeyword.getKeyword()))
                        .toList(),
                topic.getChoices().stream().map(ChoiceResponse::from).toList());
    }
}
