package life.offonoff.ab.repository.topic;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.repository.pagination.PagingUtil;
import life.offonoff.ab.service.request.TopicSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

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
        return sort.stream()
                .map(o -> {
                            Order order = o.isAscending() ? Order.ASC : Order.DESC;
                            PathBuilder path = new PathBuilder(Topic.class, "topic");
                            return new OrderSpecifier(order, path.get(o.getProperty()));
                        }
                ).toList()
                .toArray(OrderSpecifier[]::new);
    }
}
