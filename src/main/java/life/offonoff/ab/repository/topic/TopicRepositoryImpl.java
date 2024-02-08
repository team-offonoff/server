package life.offonoff.ab.repository.topic;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import life.offonoff.ab.application.service.request.TopicSearchRequest;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.repository.pagination.PagingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

import static life.offonoff.ab.domain.topic.QTopic.topic;
import static life.offonoff.ab.domain.topic.hide.QHiddenTopic.hiddenTopic;
import static life.offonoff.ab.repository.topic.booleanexpression.TopicBooleanExpression.*;

@RequiredArgsConstructor
@Repository
public class TopicRepositoryImpl implements TopicRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Topic> findAll(Long memberId, TopicSearchRequest request, Pageable pageable) {

        JPAQuery<Topic> baseQuery = queryFactory.selectFrom(topic)
                                                .join(topic.author).fetchJoin()
                                                .leftJoin(topic.keyword).fetchJoin()
                                                .where(eqTopicStatus(request.getStatus()),
                                                        eqKeyword(request.getKeywordId()),
                                                        eqTopicSide(request.getSide()))
                                                .orderBy(TopicOrderBy.getOrderSpecifiers(pageable.getSort()))
                                                .offset(pageable.getOffset())
                                                .limit(pageable.getPageSize() + 1);
        hideForMember(memberId, baseQuery);

        List<Topic> result = baseQuery.fetch();

        return PagingUtil.toSlice(result, pageable);
    }

    private void hideForMember(Long memberId, JPAQuery<Topic> query) {
        if (memberId != null) {
            query = query.leftJoin(hiddenTopic).on(hiddenTopic.topic.id.eq(topic.id),
                                                   hiddenTopic.member.id.eq(memberId))
                         .where(hiddenTopic.member.id.isNull());
        }
    }

    @Override
    public List<Topic> findAll(TopicSearchCond cond) {
        return queryFactory
                .select(topic)
                .from(topic)
                .where(
                        eqTopicStatus(cond.topicStatus()),
                        gtDeadline(cond.startCompareTime()),
                        ltDeadline(cond.endCompareTime())
                ).fetch();
    }



    static class TopicOrderBy {

        private static OrderSpecifier[] getOrderSpecifiers(Sort sort) {
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
    }
}
