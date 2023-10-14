package life.offonoff.ab.exception;

public abstract class AbException extends RuntimeException {

    public AbException(String message) {
        super(message);
    }

    /**
     * 체크 에외 -> 언체크 예외 변환 시 필요
     */
    public AbException(String message, Exception cause) {
        super(message, cause);
    }

    public abstract String getHint();
    public abstract int getHttpStatusCode();
    public abstract AbCode getAbCode();
}
