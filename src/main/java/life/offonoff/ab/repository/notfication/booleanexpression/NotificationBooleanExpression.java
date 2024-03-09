package life.offonoff.ab.repository.notfication.booleanexpression;

import com.querydsl.core.types.dsl.BooleanExpression;

import static life.offonoff.ab.domain.notification.QNotification.notification;

public class NotificationBooleanExpression {

    public static BooleanExpression eqReceiverType(String receiverType) {
        if (receiverType == null) {
            return null;
        }
        return notification.receiverType.eq(receiverType);
    }
}
