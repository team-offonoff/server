package life.offonoff.ab.exception;

import org.springframework.http.HttpStatus;

public abstract class NotFoundException extends AbException {
    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public int getHttpStatusCode() {
        return HttpStatus.NOT_FOUND.value();
    }
}
