package life.offonoff.ab.exception;

public class CommentNotFoundException extends NotFoundException {
    private static final String MESSAGE = "존재하지 않는 댓글 입니다.";
    private static final AbCode AB_CODE = AbCode.COMMENT_NOT_FOUND;
    private final Long commentId;

    public CommentNotFoundException(final Long commentId) {
        super(MESSAGE);
        this.commentId = commentId;
    }

    @Override
    public String getHint() {
        return "댓글[id=" + commentId + "]";
    }

    @Override
    public AbCode getAbCode() {
        return AB_CODE;
    }
}
