package life.offonoff.ab.application.service.auth.oauth.profile;

import life.offonoff.ab.application.service.request.auth.SignInRequest;
import life.offonoff.ab.application.service.request.auth.SignUpRequest;
import life.offonoff.ab.domain.member.Provider;

public class GoogleProfile extends OAuthProfile {

    @Override
    public SignUpRequest toSignUpRequest() {
        return new SignUpRequest(getEmail(), getSub(), Provider.GOOGLE);
    }

    @Override
    public SignInRequest toSignInRequest() {
        return new SignInRequest(getEmail(), getSub());
    }
}
