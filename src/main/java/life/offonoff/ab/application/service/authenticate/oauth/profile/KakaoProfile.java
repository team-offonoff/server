package life.offonoff.ab.application.service.authenticate.oauth.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import life.offonoff.ab.application.service.request.SignInRequest;
import life.offonoff.ab.application.service.request.SignUpRequest;
import life.offonoff.ab.domain.member.Provider;
import lombok.Getter;

@Getter
public class KakaoProfile extends OAuthProfile {

//    private String nickname;
//    private String aud;
//    private String sub;
//    @JsonProperty(value = "auth_time")
//    private String authTime;
//    @JsonProperty(value = "exp")
//    private Long expiresAt;
//    @JsonProperty(value = "iat")
//    private Long issuedAt;
//    @JsonProperty(value = "iss")
//    private String issuer;

    @Override
    public SignUpRequest toSignUpRequest() {
        return new SignUpRequest(getEmail(), null, Provider.KAKAO);
    }

    @Override
    public SignInRequest toSignInRequest() {
        return new SignInRequest(getEmail(), null);
    }
}
