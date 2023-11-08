package life.offonoff.ab.exception;

public class DuplicateException extends AbException {

    public DuplicateException(String message) {
        super(message);
    }

    public DuplicateException(String message, Exception cause) {
        super(message, cause);
    }

    @Override
    public String getHint() {
        return null;
    }

    @Override
    public int getHttpStatusCode() {
        return 0;
    }

    @Override
    public AbCode getAbCode() {
        return null;
    }
}
