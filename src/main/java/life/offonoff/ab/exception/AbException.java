package life.offonoff.ab.exception;

public abstract class AbException extends RuntimeException {
    public AbException(String message) {
        super(message);
    }

    public abstract String getHint();
    public abstract int getHttpStatusCode();
    public abstract AbCode getAbCode();
}
