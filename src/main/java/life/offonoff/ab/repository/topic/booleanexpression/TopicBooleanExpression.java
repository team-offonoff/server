package life.offonoff.ab.repository.topic.booleanexpression;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import life.offonoff.ab.domain.keyword.QKeyword;
import life.offonoff.ab.domain.topic.QTopicKeyword;
import life.offonoff.ab.domain.topic.TopicKeyword;
import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.domain.topic.hide.HiddenTopic;

import java.time.LocalDateTime;

import static life.offonoff.ab.domain.keyword.QKeyword.keyword;
import static life.offonoff.ab.domain.topic.QTopic.topic;
import static life.offonoff.ab.domain.topic.QTopicKeyword.topicKeyword;
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

    public static BooleanExpression eqKeyword(Long keywordId) {
         return keywordId != null ? topic.id.in(
                 JPAExpressions.selectFrom(topicKeyword)
                               .where(topicKeyword.keyword.id.eq(keywordId))
                               .fetch()
                               .stream()
                               .map(tk -> tk.getTopic().getId())
                               .toList())
                 : null;
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
