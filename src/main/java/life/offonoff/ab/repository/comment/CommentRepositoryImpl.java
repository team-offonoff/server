package life.offonoff.ab.repository.comment;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.repository.pagination.PagingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

import static life.offonoff.ab.domain.comment.QComment.*;
import static life.offonoff.ab.domain.topic.QTopic.*;

@RequiredArgsConstructor
@Repository
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Comment> findAll(Long topicId, Pageable pageable) {
        List<Comment> comments = queryFactory
                .selectFrom(comment)
                .join(comment.topic)
                .join(comment.writer).fetchJoin()
                .where(topic.id.eq(topicId))
                .orderBy(CommentOrderBy.getOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return PagingUtil.toSlice(comments, pageable);
    }

    static class CommentOrderBy {

        private static OrderSpecifier[] getOrderSpecifiers(Sort sort) {
            if (sort == null) {
                return null;
            }

            return sort.stream()
                    .map(o -> {
                                Order order = o.isAscending() ? Order.ASC : Order.DESC;
                                PathBuilder path = new PathBuilder(Comment.class, "comment");
                                return new OrderSpecifier(order, path.get(o.getProperty()));
                            }
                    ).toList()
                    .toArray(OrderSpecifier[]::new);
        }
    }
}
