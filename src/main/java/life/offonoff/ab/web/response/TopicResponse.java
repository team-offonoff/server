package life.offonoff.ab.web.response;

import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;

import java.util.List;

public record TopicResponse(
        Long topicId,
        TopicSide topicSide,
        String title,
        Long categoryId,
        List<ChoiceResponse> choices
) {
    public static TopicResponse from(Topic topic) {
        return new TopicResponse(
                topic.getId(),
                topic.getSide(),
                topic.getTitle(),
                topic.getCategory().getId(),
                topic.getChoices().stream().map(ChoiceResponse::from).toList());
    }
}
