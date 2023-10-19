package life.offonoff.ab.repository.topic;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.repository.pagination.PagingUtil;
import life.offonoff.ab.application.service.request.TopicSearchRequest;
import life.offonoff.ab.application.service.vote.votingtopic.VotingTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.querydsl.core.types.Projections.*;
import static life.offonoff.ab.domain.topic.QTopic.*;
import static life.offonoff.ab.repository.topic.booleanexpression.TopicBooleanExpression.*;

@RequiredArgsConstructor
@Repository
public class TopicRepositoryImpl implements TopicRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Topic> findAll(TopicSearchRequest request, Pageable pageable) {
        List<Topic> result = queryFactory
                .selectFrom(topic)
                .join(topic.publishMember).fetchJoin()
                .where(
                        eqTopicStatus(request.getTopicStatus()),
                        eqCategory(request.getCategoryId()),
                        hideOrNot(request.getMemberId(), request.getHidden())
                )
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return PagingUtil.toSlice(result, pageable);
    }

    private OrderSpecifier[] getOrderSpecifiers(Sort sort) {
        if (sort == null) {
            return null;
        }

        return sort.stream()
                .map(o -> {
                            Order order = o.isAscending() ? Order.ASC : Order.DESC;
                            PathBuilder path = new PathBuilder(Topic.class, "topic");
                            return new OrderSpecifier(order, path.get(o.getProperty()));
                        }
                ).toList()
                .toArray(OrderSpecifier[]::new);
    }

    @Override
    public List<VotingTopic> findAllInVoting(LocalDateTime time) {
        return queryFactory
                .select(constructor(VotingTopic.class,
                                topic.id,
                                topic.deadline
                        )
                )
                .from(topic)
                .where(topic.deadline.after(time))
                .fetch();
    }

    @Override
    public void updateStatus(Long topicId, TopicStatus topicStatus) {
        queryFactory
                .update(topic)
                .set(topic.status, topicStatus)
                .where(topic.id.eq(topicId))
                .execute();
    }

}
