package life.offonoff.ab.application.service.authenticate;

import life.offonoff.ab.application.service.authenticate.oauth.OAuthTemplate;
import life.offonoff.ab.application.service.authenticate.oauth.profile.OAuthProfile;
import life.offonoff.ab.application.service.request.SignInRequest;
import life.offonoff.ab.application.service.request.SignUpRequest;
import life.offonoff.ab.application.service.request.auth.OAuthRequest;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.web.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OAuthService {

    private final AuthService authService;
    private final MemberRepository memberRepository;

    private final OAuthTemplate oAuthTemplate;

    @Transactional
    public OAuthResponse authorize(OAuthRequest request) {

        OAuthProfile oAuthProfile = oAuthTemplate.getOAuthProfile(request);

        if (isNewMember(oAuthProfile)) {
            return joinOAuthProfile(oAuthProfile);
        }
        return loginOAuthProfile(oAuthProfile);
    }

    private boolean isNewMember(OAuthProfile profile) {
        return memberRepository.findByEmail(profile.getEmail())
                               .isEmpty();
    }

    private OAuthSignUpResponse joinOAuthProfile(OAuthProfile oAuthProfile) {

        SignUpRequest request = oAuthProfile.toSignUpRequest();

        SignUpResponse response = authService.signUp(request);
        return new OAuthSignUpResponse(true, response.getAccessToken());
    }

    private OAuthSignInResponse loginOAuthProfile(OAuthProfile oAuthProfile) {

        SignInRequest request = oAuthProfile.toSignInRequest();

        SignInResponse response = authService.signIn(request);
        return new OAuthSignInResponse(false, response.getAccessToken());
    }
}
