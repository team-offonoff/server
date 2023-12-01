package life.offonoff.ab.web.response;

import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.topic.TopicSide;

public record KeywordResponse(
        Long keywordId,
        String keywordName,
        TopicSide topicSide
) {
    public static KeywordResponse from(Keyword keyword) {
        return new KeywordResponse(keyword.getId(), keyword.getName(), keyword.getSide());
    }
}
