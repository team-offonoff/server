package life.offonoff.ab.application.service.request.auth;

import lombok.Getter;

@Getter
public class KakaoAuthRequest extends OAuthRequest {
    private String authorizeCode;
    private String redirectUri;

    public KakaoAuthRequest(String authorizeCode, String redirectUri) {
        this.authorizeCode = authorizeCode;
        this.redirectUri = redirectUri;
    }
}
