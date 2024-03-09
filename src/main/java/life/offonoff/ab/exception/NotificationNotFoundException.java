package life.offonoff.ab.exception;


public abstract class NotificationNotFoundException extends NotFoundException {
    private static final String MESSAGE = "존재하지 않는 알림 입니다.";

    public NotificationNotFoundException() {
        super(MESSAGE);
    }
}
