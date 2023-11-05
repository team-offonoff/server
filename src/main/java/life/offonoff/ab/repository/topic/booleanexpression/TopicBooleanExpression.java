package life.offonoff.ab.repository.topic.booleanexpression;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.domain.topic.hide.HiddenTopic;

import java.time.LocalDateTime;

import static life.offonoff.ab.domain.topic.QTopic.topic;
import static life.offonoff.ab.domain.topic.hide.QHiddenTopic.hiddenTopic;

public class TopicBooleanExpression {

    public static BooleanExpression eqTopicStatus(TopicStatus topicStatus) {
        return topicStatus != null ? topic.status.eq(topicStatus) : null;
    }

    public static BooleanExpression gtDeadline(LocalDateTime compareTime) {
        return compareTime != null ? topic.deadline.gt(compareTime) : null;
    }

    public static BooleanExpression ltDeadline(LocalDateTime compareTime) {
        return compareTime != null ? topic.deadline.lt(compareTime) : null;
    }

    public static BooleanExpression eqCategory(Long categoryId) {
        return categoryId != null ? topic.category.id.eq(categoryId) : null;
    }

    public static BooleanExpression hideOrNot(Long memberId, Boolean hidden) {
        if (hidden == null) {
            return null;
        }

        JPQLQuery<HiddenTopic> subquery = hiddenBySubquery(memberId);

        if (hidden) {
            return subquery.exists();
        }
        return subquery.notExists();
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
