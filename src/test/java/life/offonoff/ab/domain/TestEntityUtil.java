package life.offonoff.ab.domain;

import life.offonoff.ab.domain.category.Category;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.member.NotificationEnabled;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.choice.Choice;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.domain.topic.choice.content.ChoiceContent;
import life.offonoff.ab.domain.vote.Vote;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static Topic createAssociatedTopic(int seq, TopicSide side) {
        Member member = createMember(seq);
        Category category = createCategory(seq);
        Topic topic = createTopic(seq, side);
        topic.associate(member, category, null);
        return topic;
    }

    public static List<Topic> creatAssociatedTopicList(int length, TopicSide side) {
        List<Topic> topics = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            topics.add(createAssociatedTopic(i, side));
        }

        return topics;
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
    public static Choice createChoice(Topic topic, ChoiceOption option, ChoiceContent content) {
        return new Choice(topic, option, content);
    }

    //== Pageable ==//
    public static Pageable createPageableDesc(int page, int size, String property) {
        return PageRequest.of(page, size, Sort.Direction.DESC, property);
    }
}
