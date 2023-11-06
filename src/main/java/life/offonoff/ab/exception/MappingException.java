package life.offonoff.ab.exception;

public abstract class MappingException extends AbException {

    public MappingException(String message) {
        super(message);
    }

    public MappingException(String message, Exception cause) {
        super(message, cause);
    }
}
