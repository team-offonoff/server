package life.offonoff.ab.exception;

public class VoteConcurrencyException extends ConcurrencyViolationException {
    private static final String HINT = "투표 시 동시성 문제 발생";

    @Override
    public String getHint() {
        return HINT;
    }
}
