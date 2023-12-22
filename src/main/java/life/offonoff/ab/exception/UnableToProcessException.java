package life.offonoff.ab.exception;

import org.springframework.http.HttpStatus;

public abstract class UnableToProcessException extends AbException {

    public UnableToProcessException(String message) {
        super(message);
    }

    @Override
    public int getHttpStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
