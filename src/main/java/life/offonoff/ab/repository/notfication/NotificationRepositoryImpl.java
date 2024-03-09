package life.offonoff.ab.repository.notfication;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import life.offonoff.ab.application.service.request.NotificationRequest;
import life.offonoff.ab.domain.member.QMember;
import life.offonoff.ab.domain.notification.Notification;
import life.offonoff.ab.domain.notification.QNotification;
import life.offonoff.ab.domain.notification.VoteResultNotification;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.repository.notfication.booleanexpression.NotificationBooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

import static life.offonoff.ab.domain.member.QMember.member;
import static life.offonoff.ab.domain.notification.QNotification.notification;
import static life.offonoff.ab.repository.notfication.booleanexpression.NotificationBooleanExpression.eqReceiverType;

@RequiredArgsConstructor
@Repository
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final NotificationJdbcRepositoryImpl notificationJdbcRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public void saveAll(List<VoteResultNotification> notifications) {
        notificationJdbcRepository.saveVoteResultNotificationsInBatch(notifications);
    }

    @Override
    public List<Notification> findAllOrderByCreatedAtDesc(Long receiverId, NotificationRequest request) {
        return queryFactory
                .select(notification)
                .from(notification)
                .join(member).on(member.id.eq(receiverId))
                .where(eqReceiverType(request.getReceiver()))
                .orderBy(notification.createdAt.desc())
                .fetch();
    }
}
