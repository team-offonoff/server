package life.offonoff.ab.repository.keyword;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.keyword.QKeyword;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.repository.pagination.PagingUtil;
import life.offonoff.ab.repository.topic.TopicRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

import static life.offonoff.ab.domain.keyword.QKeyword.keyword;

@RequiredArgsConstructor
@Repository
public class KeywordRepositoryImpl implements KeywordRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Keyword> findAllByTopicSide(TopicSide side, Pageable pageable) {

        List<Keyword> keywords = queryFactory
                .selectFrom(keyword)
                .where(keyword.side.eq(side))
                .orderBy(KeywordRepositoryImpl.KeywordOrderBy.getOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return PagingUtil.toSlice(keywords, pageable);
    }

    static class KeywordOrderBy {

        private static OrderSpecifier[] getOrderSpecifiers(Sort sort) {
            if (sort == null) {
                return null;
            }

            return sort.stream()
                    .map(o -> {
                                Order order = o.isAscending() ? Order.ASC : Order.DESC;
                                PathBuilder path = new PathBuilder(Keyword.class, "keyword");
                                return new OrderSpecifier(order, path.get(o.getProperty()));
                            }
                    ).toList()
                    .toArray(OrderSpecifier[]::new);
        }
    }
}
