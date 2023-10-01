package life.offonoff.ab.web.common;

public record ErrorContent(String message, String hint, int httpCode) {
    public static ErrorContent of(final String message, final String hint, final int httpCode) {
        return new ErrorContent(message, hint, httpCode);
    }

    public static ErrorContent of(final String hint, final int httpCode) {
        return new ErrorContent(null, hint, httpCode);
    }
}
