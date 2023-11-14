package life.offonoff.ab.exception;

import org.springframework.http.HttpStatus;

public abstract class IllegalArgumentException extends AbException {

    public IllegalArgumentException(String message) {
        super(message);
    }

    @Override
    public int getHttpStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
