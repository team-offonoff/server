package life.offonoff.ab.exception;

public class IllegalTopicStatusChangeException extends IllegalArgumentException {
    private static final String MESSAGE = "올바르지 않은 토픽 활성화 요청입니다.";
    private Long memberId;
    private Long topicId;

    public IllegalTopicStatusChangeException(Long memberId, Long topicId) {
        super(MESSAGE);
        this.memberId = memberId;
        this.topicId = topicId;
    }

    @Override
    public String getHint() {
        return "멤버[id=" + memberId + "]가 작성한 토픽[id=" + topicId + "]이 아님.";
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.ILLEGAL_TOPIC_STATUS_CHANGE;
    }
}
