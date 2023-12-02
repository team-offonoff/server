package life.offonoff.ab.exception;

import org.springframework.http.HttpStatus;

public abstract class DuplicateException extends AbException {

    public DuplicateException(String message) {
        super(message);
    }

    @Override
    public int getHttpStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
