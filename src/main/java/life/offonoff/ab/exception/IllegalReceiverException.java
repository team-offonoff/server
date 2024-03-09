package life.offonoff.ab.exception;

public class IllegalReceiverException extends IllegalArgumentException {

    private static final String MESSAGE = "알림 수신자가 아닙니다.";
    private final Long memberId;
    private final Long notificationId;

    public IllegalReceiverException(Long memberId, Long notificationId) {
        super(MESSAGE);
        this.memberId = memberId;
        this.notificationId = notificationId;
    }

    @Override
    public String getHint() {
        return "회원[id : " + memberId + "]은 알림[id : " + notificationId + "]의 수신자가 아닙니다.";
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.ILLEGAL_RECEIVER;
    }
}
