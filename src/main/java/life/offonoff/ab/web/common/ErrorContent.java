package life.offonoff.ab.web.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorContent(String message, String hint, int httpCode, Object payload) {

    public static ErrorContent of(final String message, final int httpCode) {
        return new ErrorContent(null, message, httpCode, null);
    }

    public static ErrorContent of(final String message, final String hint, final int httpCode) {
        return new ErrorContent(message, hint, httpCode, null);
    }

    public static ErrorContent of(final String message, final String hint, final int httpCode, final Object payload) {
        return new ErrorContent(message, hint, httpCode, payload);
    }
}
