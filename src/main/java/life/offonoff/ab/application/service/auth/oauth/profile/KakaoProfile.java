package life.offonoff.ab.application.service.auth.oauth.profile;

import life.offonoff.ab.application.service.request.auth.SignInRequest;
import life.offonoff.ab.application.service.request.auth.SignUpRequest;
import life.offonoff.ab.domain.member.Provider;
import lombok.Getter;

@Getter
public class KakaoProfile extends OAuthProfile {

//    private String nickname;
//    private String aud;
    private String sub;
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
        return new SignUpRequest(getEmail(), sub, Provider.KAKAO);
    }

    @Override
    public SignInRequest toSignInRequest() {
        return new SignInRequest(getEmail(), sub);
    }
}
