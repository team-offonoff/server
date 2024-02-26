package life.offonoff.ab.web.response.topic;

import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.web.response.MemberResponse;
import life.offonoff.ab.web.response.KeywordResponse;
import life.offonoff.ab.web.response.topic.choice.ChoiceResponse;
import life.offonoff.ab.web.response.topic.choice.ChoiceResponsesFactory;
import life.offonoff.ab.web.response.topic.content.TopicContentResponseFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static life.offonoff.ab.web.response.topic.content.TopicContentResponseFactory.*;

@Getter
@Builder
@AllArgsConstructor
public class TopicResponse {
    private Long topicId;
    private TopicSide topicSide;
    private String topicTitle;
    private Long deadline;
    private int voteCount;
    private int commentCount;
    private TopicContentResponse topicContent;
    private KeywordResponse keyword;
    private List<? extends ChoiceResponse> choices;
    private MemberResponse author;
    private Long createdAt;
    private ChoiceOption selectedOption;

    public TopicResponse(Topic topic, Member retriever) {
        this.topicId = topic.getId();
        this.topicSide = topic.getSide();
        this.topicTitle = topic.getTitle();
        this.deadline = topic.getDeadlineSecond();
        this.commentCount = topic.getCommentCount();
        this.voteCount = topic.getVoteCount();
        this.topicContent = TopicContentResponseFactory.create(topic.getContent());
        this.keyword = KeywordResponse.from(topic.getKeyword());
        this.choices = ChoiceResponsesFactory.create(topic);
        this.author = MemberResponse.from(topic.getAuthor());
        this.createdAt = topic.getCreatedSecond();
        this.selectedOption = getSelectedOption(topic, retriever);
    }

    private ChoiceOption getSelectedOption(Topic topic, Member retriever) {
        if (retriever == null) {
            return null;
        }
        return retriever.getVotedOptionOfTopic(topic);
    }

    public static TopicResponse from(Topic topic) {
        return new TopicResponse(topic, null);
    }

    public static TopicResponse from(Topic topic, Member retriever) {
        // TODO : topics.size * votes.size 시간 복잡도 개선
        return new TopicResponse(topic, retriever);
    }
}
