package life.offonoff.ab.repository.topic.specification;

import jakarta.persistence.criteria.*;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.domain.topic.hide.HiddenTopic;
import org.springframework.data.jpa.domain.Specification;

public class TopicSpecifications {

    public static Specification<Topic> keyword(Long keywordId) {
        return (t, query, cb) -> cb.equal(t.<Keyword>get("keyword")
                                           .<Long>get("id"), keywordId);
    }

    public static Specification<Topic> status(TopicStatus status) {
        return (t, query, cb) -> cb.equal(t.<TopicStatus>get("status"), status);
    }

    public static Specification<Topic> hiddenOrNotByMember(Boolean hidden, Long memberId) {
        if (hidden) {
            return hiddenByMember(memberId);
        }
        return openedToMember(memberId);
    }

    private static Specification<Topic> hiddenByMember(Long memberId) {
        return (t, query, cb) -> {
            if (query.getResultType().equals(Topic.class)) { // data query 일 때
                t.fetch("author", JoinType.INNER);
            }

            Join<Topic, HiddenTopic> ht = t.join("hides", JoinType.INNER);
            return cb.equal(
                    ht.get("member").get("id"),
                    memberId
            );
        };
    }

    private static Specification<Topic> openedToMember(Long memberId) {
        return (t, query, cb) -> {
            // fetch join with publish Member
            if (query.getResultType().equals(Topic.class)) { // data query 일 때
                t.fetch("author", JoinType.INNER);
            }

            // subquery (not hidden by memberId)
            Subquery<HiddenTopic> subquery = query.subquery(HiddenTopic.class);
            Root<HiddenTopic> ht = subquery.from(HiddenTopic.class);
            subquery.select(ht)
                    .where(cb.and(
                            cb.equal(ht.get("id"), t.get("id")),
                            cb.equal(ht.get("member").get("id"), memberId)
                        )
                    );
            // not exists Topic
            return cb.not(cb.exists(subquery));
        };
    }

}
