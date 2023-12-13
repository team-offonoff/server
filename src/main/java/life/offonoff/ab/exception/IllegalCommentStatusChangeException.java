package life.offonoff.ab.exception;

public class IllegalCommentStatusChangeException extends IllegalArgumentException {
    private static final String MESSAGE = "올바르지 않은 댓글 활성화 요청입니다.";
    private final Long memberId;
    private final Long commentId;

    public IllegalCommentStatusChangeException(Long memberId, Long commentId) {
        super(MESSAGE);
        this.memberId = memberId;
        this.commentId = commentId;
    }

    @Override
    public String getHint() {
        return "멤버[id=" + memberId + "]가 작성한 댓글[id=" + commentId + "]이 아님.";
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.ILLEGAL_COMMENT_STATUS_CHANGE;
    }
}
