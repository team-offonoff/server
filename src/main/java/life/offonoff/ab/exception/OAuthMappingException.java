package life.offonoff.ab.exception;

public class OAuthMappingException extends MappingException {

    public OAuthMappingException(String message) {
        super(message);
    }

    public OAuthMappingException(String message, Exception cause) {
        super(message, cause);
    }

    @Override
    public String getHint() {
        return "KAKAO OAUTH 인가 요청에 대한 응답을 확인해주세요.";
    }

    @Override
    public int getHttpStatusCode() {
        return 400;
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.INVALID_KAKAO_OAUTH_MAPPING;
    }
}
