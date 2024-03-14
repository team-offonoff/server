package life.offonoff.ab.exception;

public class CommentConcurrencyException extends ConcurrencyViolationException {
    private static final String HINT = "토픽의 댓글수 업데이트 중 낙관적 락 실패";
    @Override
    public String getHint() {
        return HINT;
    }
}
