package life.offonoff.ab.application.service.authenticate;

import life.offonoff.ab.application.service.authenticate.oauth.OAuthTemplate;
import life.offonoff.ab.application.service.authenticate.oauth.profile.KakaoProfile;
import life.offonoff.ab.application.service.request.SignInRequest;
import life.offonoff.ab.application.service.request.SignUpRequest;
import life.offonoff.ab.application.service.request.auth.KakaoAuthRequest;
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
    public OAuthResponse authenticate(KakaoAuthRequest request) {

        KakaoProfile kakaoProfile = oAuthTemplate.getKakaoProfile(request);

        if (isNewMember(kakaoProfile)) {
            return joinOAuthProfile(kakaoProfile);
        }
        return loginOAuthProfile(kakaoProfile);
    }

    private boolean isNewMember(KakaoProfile profile) {
        return memberRepository.findByEmail(profile.getEmail())
                               .isEmpty();
    }

    private OAuthSignUpResponse joinOAuthProfile(KakaoProfile kakaoProfile) {
        SignUpRequest request = new SignUpRequest(kakaoProfile.getEmail(), createRandomOAuthPassword(), "kakao");

        SignUpResponse response = authService.signUp(request);
        return new OAuthSignUpResponse(true, response.getAccessToken());
    }

    private OAuthSignInResponse loginOAuthProfile(KakaoProfile kakaoProfile) {
        SignInRequest request = new SignInRequest(kakaoProfile.getEmail(), createRandomOAuthPassword());

        SignInResponse response = authService.signIn(request);
        return new OAuthSignInResponse(false, response.getAccessToken());
    }

    private String createRandomOAuthPassword() {
        return "oauth-password";
    }
}
