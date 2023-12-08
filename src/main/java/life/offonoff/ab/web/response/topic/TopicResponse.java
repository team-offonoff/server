package life.offonoff.ab.web.response.topic;

import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.web.response.AuthorResponse;
import life.offonoff.ab.web.response.KeywordResponse;
import life.offonoff.ab.web.response.topic.choice.ChoiceResponse;
import life.offonoff.ab.web.response.topic.content.TopicContentResponseFactory;
import lombok.Builder;

import java.util.List;

import static life.offonoff.ab.web.response.topic.content.TopicContentResponseFactory.*;

@Builder
public record TopicResponse(
        Long topicId,
        TopicSide topicSide,
        String topicTitle,
        Long deadline,
        int voteCount,
        TopicContentResponse topicContent,
        KeywordResponse keyword,
        List<ChoiceResponse> choices,
        AuthorResponse author
) {
    public static TopicResponse from(Topic topic) {
        return new TopicResponse(
                topic.getId(),
                topic.getSide(),
                topic.getTitle(),
                topic.getDeadlineSecond(),
                topic.getVoteCount(),
                TopicContentResponseFactory.create(topic.getContent()),
                KeywordResponse.from(topic.getKeyword()),
                topic.getChoices().stream().map(ChoiceResponse::from).toList(),
                AuthorResponse.from(topic.getAuthor()));
    }


}
