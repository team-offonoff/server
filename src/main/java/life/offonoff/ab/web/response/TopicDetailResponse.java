package life.offonoff.ab.web.response;

import life.offonoff.ab.domain.topic.Topic;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TopicDetailResponse {

    private Long topicId;
    private String title;
    private Long publishMemberId;
    private String publishMemberNickname;
    private Long deadline;
    private int voteCount;
    private int commentCount;

    public TopicDetailResponse(Topic topic) {
        this.topicId = topic.getId();
        this.title = topic.getTitle();
        this.publishMemberId = topic.getPublishMember().getId();
        this.publishMemberNickname = topic.getPublishMember().getNickname();
        this.deadline = topic.getDeadlineMillis();
        this.voteCount = topic.getVoteCount();
        this.commentCount = topic.getCommentCount();
    }
}
