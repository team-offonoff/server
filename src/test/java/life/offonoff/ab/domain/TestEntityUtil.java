package life.offonoff.ab.domain;

import life.offonoff.ab.domain.category.Category;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.member.NotificationEnabled;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.choice.Choice;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.domain.topic.content.TopicContent;
import life.offonoff.ab.domain.vote.Vote;

public class TestEntityUtil {

    //== Member ==//
    public static Member createMember(int seq) {
        String name = "MEMBER_" + seq;
        String nickname = "NICKNAME_" + seq;
        NotificationEnabled notificationEnabled = new NotificationEnabled(true, true, true, true);
        return new Member(name, nickname, notificationEnabled);
    }

    //== Topic ==//
    public static Topic createTopic(int seq, TopicSide side) {
        String topicTitle = "TITLE_" + seq;
        return new Topic(topicTitle, side);
    }

    //== Category ==//
    public static Category createCategory(int seq) {
        String name = "CATEGORY_" + seq;
        return new Category(name);
    }

    //== Comment ==//
    public static Comment createComment(int seq) {
        String content = "CONTENT_" + seq;
        return new Comment(content);
    }

    //== Vote ==//
    public static Vote createVote(ChoiceOption option) {
        return new Vote(option);
    }

    //== Choice ==//
    public static Choice createChoice(ChoiceOption option) {
        return new Choice(option);
    }
}
