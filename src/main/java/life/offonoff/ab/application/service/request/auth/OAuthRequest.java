package life.offonoff.ab.application.service.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Null;
import life.offonoff.ab.domain.member.Provider;
import lombok.Getter;

@Getter
public class OAuthRequest {

    private AuthorizeType type;

    // token 조회 -> auth
    @JsonProperty(value = "code")
    private String authorizeCode;
    @JsonProperty(value = "redirect_uri")
    private String redirectUri;

    // auth
    @JsonProperty(value = "id_token")
    private String idToken;

    // controller에서 바인딩
    @Null
    private Provider provider;

    public OAuthRequest(AuthorizeType type, String authorizeCode, String redirectUri, String idToken) {
        this.type = type;
        this.authorizeCode = authorizeCode;
        this.redirectUri = redirectUri;
        this.idToken = idToken;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }
}
