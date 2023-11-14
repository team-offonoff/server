package life.offonoff.ab.application.service.auth.oauth.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import life.offonoff.ab.application.service.request.auth.SignInRequest;
import life.offonoff.ab.application.service.request.auth.SignUpRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class OAuthProfile {

    private String email;
    private String sub;

    public abstract SignUpRequest toSignUpRequest();

    public abstract SignInRequest toSignInRequest();
}
