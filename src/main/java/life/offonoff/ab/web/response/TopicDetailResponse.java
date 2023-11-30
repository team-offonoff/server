package life.offonoff.ab.web.response;

import life.offonoff.ab.domain.topic.Topic;
import lombok.Getter;

@Getter
public class TopicDetailResponse {

    private Long topicId;
    private String title;
    private Long authorId;
    private String authorNickname;
    private Long deadline;
    private int voteCount;
    private int commentCount;

    public TopicDetailResponse(Topic topic) {
        this.topicId = topic.getId();
        this.title = topic.getTitle();
        this.authorId = topic.getAuthor().getId();
        this.authorNickname = topic.getAuthor().getNickname();
        this.deadline = topic.getDeadlineSecond();
        this.voteCount = topic.getVoteCount();
        this.commentCount = topic.getCommentCount();
    }
}
