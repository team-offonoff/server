package life.offonoff.ab.application.service.authenticate.oauth.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class KakaoProfile extends OAuthProfile {

    private String email;
    private String nickname;
    private String aud;
    private String sub;
    @JsonProperty(value = "auth_time")
    private String authTime;
    @JsonProperty(value = "exp")
    private Long expiresAt;
    @JsonProperty(value = "iat")
    private Long issuedAt;
    @JsonProperty(value = "iss")
    private String issuer;
}
