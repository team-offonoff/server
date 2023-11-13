package life.offonoff.ab.application.service.request.auth;

import jakarta.validation.constraints.Null;
import life.offonoff.ab.domain.member.Provider;
import lombok.Getter;

@Getter
public class OAuthRequest {

    private AuthorizeType type;

    // token 조회 -> auth
    private String authorizeCode;
    private String redirectUri;

    // auth
    private String idToken;

    // controller에서 바인딩
    @Null
    private Provider provider;

    public void setProvider(Provider provider) {
        this.provider = provider;
    }
}
