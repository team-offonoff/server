package life.offonoff.ab.application.service.authenticate.oauth.profile;

import life.offonoff.ab.application.service.request.SignInRequest;
import life.offonoff.ab.application.service.request.SignUpRequest;
import life.offonoff.ab.domain.member.Provider;

public class GoogleProfile extends OAuthProfile {

    @Override
    public SignUpRequest toSignUpRequest() {
        return new SignUpRequest(getEmail(), null, Provider.GOOGLE);
    }

    @Override
    public SignInRequest toSignInRequest() {
        return new SignInRequest(getEmail(), null);
    }
}
