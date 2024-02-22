package life.offonoff.ab.exception;

public class CommentReportDuplicateException extends DuplicateException {
    private static final String MESSAGE = "이미 신고한 댓글입니다.";
    private final Long commentId;
    private final Long memberId;

    public CommentReportDuplicateException(final Long commentId, final Long memberId) {
        super(MESSAGE);
        this.commentId = commentId;
        this.memberId = memberId;
    }

    @Override
    public String getHint() {
        return "멤버[id="+memberId+"]가 이미 신고한 댓글[id="+ commentId +"]";
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.DUPLICATE_COMMENT_REPORT;
    }
}
