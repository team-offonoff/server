package life.offonoff.ab.repository.topic.booleanexpression;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.domain.topic.hide.HiddenTopic;

import java.time.LocalDateTime;

import static life.offonoff.ab.domain.topic.QTopic.topic;
import static life.offonoff.ab.domain.topic.hide.QHiddenTopic.hiddenTopic;

public class TopicBooleanExpression {

    public static BooleanExpression eqTopicStatus(TopicStatus topicStatus) {
        TopicStatus status = TopicStatus.VOTING; // default STATUS condition

        if (topicStatus != null) {
            status = topicStatus;
        }
        return topic.status.eq(status);
    }

    public static BooleanExpression gtDeadline(LocalDateTime compareTime) {
        return compareTime != null ? topic.deadline.gt(compareTime) : null;
    }

    public static BooleanExpression ltDeadline(LocalDateTime compareTime) {
        return compareTime != null ? topic.deadline.lt(compareTime) : null;
    }

    public static BooleanExpression eqKeyword(Long keywordId) {
        return keywordId != null ? topic.keyword.id.eq(keywordId) : null;
    }

    public static BooleanExpression eqTopicSide(TopicSide side) {
        return side != null ? topic.side.eq(side) : null;
    }

    public static BooleanExpression hideFor(Long memberId) {
        if (memberId == null) {
            return null;
        }

        return hiddenBySubquery(memberId).notExists();
    }

    private static JPQLQuery<HiddenTopic> hiddenBySubquery(Long memberId) {
        return JPAExpressions
                .selectFrom(hiddenTopic)
                .where(
                        hiddenTopic.topic.id.eq(topic.id),
                        hiddenTopic.member.id.eq(memberId)
                );
    }
}
