package life.offonoff.ab.web.response.topic;

import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.web.response.MemberResponse;
import life.offonoff.ab.web.response.KeywordResponse;
import life.offonoff.ab.web.response.topic.choice.ChoiceResponse;
import life.offonoff.ab.web.response.topic.content.TopicContentResponseFactory;
import lombok.Builder;
import lombok.Getter;

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
        MemberResponse author,
        ChoiceOption selectedOption
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
                MemberResponse.from(topic.getAuthor()),
                null // 선택 option이 없을 경우 null 처리
        );
    }

    public static TopicResponse from(Topic topic, Member retriever) {
        return new TopicResponse(
                topic.getId(),
                topic.getSide(),
                topic.getTitle(),
                topic.getDeadlineSecond(),
                topic.getVoteCount(),
                TopicContentResponseFactory.create(topic.getContent()),
                KeywordResponse.from(topic.getKeyword()),
                topic.getChoices().stream().map(ChoiceResponse::from).toList(),
                MemberResponse.from(topic.getAuthor()),
                retriever.getSelectedOptionOfTopic(topic) // TODO : topics.size * votes.size 시간 복잡도 개선
        );
    }
}
