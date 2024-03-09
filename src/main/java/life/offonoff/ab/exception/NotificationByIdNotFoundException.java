package life.offonoff.ab.exception;

public class NotificationByIdNotFoundException extends NotificationNotFoundException {
    private final Long notificationId;

    public NotificationByIdNotFoundException(final Long notificationId) {
        super();
        this.notificationId = notificationId;
    }

    @Override
    public String getHint() {
        return "알림 id [" + notificationId + "]는 존재하지 않습니다.";
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.NOTIFICATION_NOT_FOUND;
    }
}
