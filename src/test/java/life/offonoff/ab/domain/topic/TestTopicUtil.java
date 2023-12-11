package life.offonoff.ab.domain.topic;

import life.offonoff.ab.domain.member.*;

public class TestTopicUtil {

    public static Topic createTopicWithAuthor(TopicSide side, Member author) {
        Topic topic = new Topic("title", side);
        topic.setAuthor(author);
        return topic;
    }

}