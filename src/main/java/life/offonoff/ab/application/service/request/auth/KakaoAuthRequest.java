package life.offonoff.ab.application.service.request.auth;

import lombok.Getter;

@Getter
public class KakaoTokenRequest extends OAuthRequest {
    private final String authorizeCode;
    private final String redirectUri;

    public KakaoTokenRequest(String authorizeCode, String redirectUri) {
        this.authorizeCode = authorizeCode;
        this.redirectUri = redirectUri;
    }
}
