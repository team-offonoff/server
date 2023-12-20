package life.offonoff.ab.exception;

public class UnableToViewCommentsException extends UnableToProcessException {
    private static final String MESSAGE = "해당 토픽의 댓글을 볼 수 없습니다.";
    private static final AbCode AB_CODE = AbCode.UNABLE_TO_VIEW_COMMENTS;
    private final Long topicId;

    public UnableToViewCommentsException(Long topicId) {
        super(MESSAGE);
        this.topicId = topicId;
    }


    @Override
    public String getHint() {
        return "댓글을 보려면 토픽[id = "+topicId+"]에 투표해야 합니다.";
    }

    @Override
    public AbCode getAbCode() {
        return AB_CODE;
    }
}
