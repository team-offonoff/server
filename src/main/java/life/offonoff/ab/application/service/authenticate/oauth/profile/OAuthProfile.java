package life.offonoff.ab.application.service.authenticate.oauth.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import life.offonoff.ab.application.service.request.SignInRequest;
import life.offonoff.ab.application.service.request.SignUpRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class OAuthProfile {

    private String email;

    public abstract SignUpRequest toSignUpRequest();

    public abstract SignInRequest toSignInRequest();
}
