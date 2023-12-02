package life.offonoff.ab.exception;

public class TopicReportDuplicateException extends DuplicateException {
    private static final String MESSAGE = "이미 신고한 토픽입니다.";
    private final Long topicId;
    private final Long memberId;

    public TopicReportDuplicateException(final Long topicId, final Long memberId) {
        super(MESSAGE);
        this.topicId = topicId;
        this.memberId = memberId;
    }

    @Override
    public String getHint() {
        return "멤버[id="+memberId+"]가 이미 신고한 토픽[id="+topicId+"]";
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.DUPLICATE_TOPIC_REPORT;
    }
}
