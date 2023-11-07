package life.offonoff.ab.application.service.authenticate;

import life.offonoff.ab.application.service.authenticate.oauth.OAuthRestTemplate;
import life.offonoff.ab.application.service.authenticate.oauth.profile.KakaoProfile;
import life.offonoff.ab.application.service.request.SignInRequest;
import life.offonoff.ab.application.service.request.SignUpRequest;
import life.offonoff.ab.application.service.request.auth.KakaoAuthRequest;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.web.response.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class OAuthService {

    private final OAuthRestTemplate oAuthTemplate;
    private final AuthService authService;
    private final MemberRepository memberRepository;

    public OAuthService(RestTemplate restTemplate, AuthService authService, MemberRepository memberRepository) {
        this.oAuthTemplate = new OAuthRestTemplate(restTemplate);
        this.authService = authService;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public OAuthResponse authenticate(KakaoAuthRequest request) {
        KakaoProfile kakaoProfile = oAuthTemplate.getKakaoProfile(request.getAuthorizeCode(), request.getRedirectUri());

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
        SignUpRequest request = new SignUpRequest(
                kakaoProfile.getEmail(),
                createRandomOAuthPassword());

        SignUpResponse response = authService.signUp(request);
        return new OAuthSignUpResponse(true, response.getAccessToken());
    }

    private OAuthSignInResponse loginOAuthProfile(KakaoProfile kakaoProfile) {
        SignInRequest request = new SignInRequest(
                kakaoProfile.getEmail(),
                createRandomOAuthPassword());

        SignInResponse response = authService.signIn(request);
        return new OAuthSignInResponse(false, response.getAccessToken());
    }

    private String createRandomOAuthPassword() {
        return "oauth-password";
    }
}
