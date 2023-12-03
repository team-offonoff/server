package life.offonoff.ab.exception.auth;

import life.offonoff.ab.exception.AbException;

public abstract class AuthorizationException extends AbException {

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(Exception exception) {
        super(exception);
    }
}
