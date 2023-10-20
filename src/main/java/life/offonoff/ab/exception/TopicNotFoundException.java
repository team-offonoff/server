package life.offonoff.ab.exception;

public class TopicNotFoundException extends NotFoundException {
    private static final String MESSAGE = "존재하지 않는 토픽 입니다.";
    private static final AbCode AB_CODE = AbCode.TOPIC_NOT_FOUND;
    private final Long topicId;

    public TopicNotFoundException(final Long topicId) {
        super(MESSAGE);
        this.topicId = topicId;
    }

    @Override
    public String getHint() {
        return "토픽 id [" + topicId + "]는 존재하지 않습니다.";
    }

    @Override
    public AbCode getAbCode() {
        return AB_CODE;
    }
}
