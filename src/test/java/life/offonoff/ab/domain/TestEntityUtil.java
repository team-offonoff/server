package life.offonoff.ab.domain;

import life.offonoff.ab.domain.category.Category;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.member.AlarmEnables;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicContent;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.choice.Choice;
import life.offonoff.ab.domain.topic.choice.ChoiceContent;
import life.offonoff.ab.domain.topic.choice.ChoiceSide;
import life.offonoff.ab.domain.vote.Vote;

public class TestEntityUtil {

    //== Member ==//
    public static Member createMember(int seq) {
        String name = "MEMBER_" + seq;
        String nickname = "NICKNAME_" + seq;
        AlarmEnables alarmEnables = new AlarmEnables(true, true, true, true);
        return new Member(name, nickname, alarmEnables);
    }

    //== Topic ==//
    public static Topic createTextTopic(int seq, TopicSide side) {
        String topicTitle = "TITLE_" + seq;
        String topicDescription = "DESCRIPTION_" + seq;

        String choiceAText = "A_TEXT_" + seq;
        String choiceBText = "B_TEXT_" + seq;

        Choice choiceA = Choice.createChoiceA(ChoiceContent.ofText(choiceAText));
        Choice choiceB = Choice.createChoiceB(ChoiceContent.ofText(choiceBText));

        TopicContent content = new TopicContent(topicTitle, topicDescription, choiceA, choiceB);
        return new Topic(side, content);
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
    public static Vote createVote(ChoiceSide side) {
        return new Vote(side);
    }
}
