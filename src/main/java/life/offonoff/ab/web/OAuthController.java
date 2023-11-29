package life.offonoff.ab.web;

import life.offonoff.ab.application.service.auth.OAuthService;
import life.offonoff.ab.application.service.request.oauth.OAuthRequest;
import life.offonoff.ab.web.response.oauth.OAuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static life.offonoff.ab.domain.member.Provider.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/oauth")
public class OAuthController {

    private final OAuthService oAuthService;

    //== OAUTH ==//

    /**
     * KAKAO 가입
     */
    @PostMapping( "/kakao/authorize")
    public ResponseEntity<OAuthResponse> authorizeKakao(@RequestBody final OAuthRequest request) {
        request.setProvider(KAKAO);
        return ResponseEntity.ok(oAuthService.authorize(request));
    }

    /**
     * GOOGLE 가입
     */
    @PostMapping( "/google/authorize")
    public ResponseEntity<OAuthResponse> authorizeGoogle(@RequestBody final OAuthRequest request) {
        request.setProvider(GOOGLE);
        return ResponseEntity.ok(oAuthService.authorize(request));
    }

    // Test Redirect Uri
    @GetMapping("/kakao/authorize")
    public String kakaoAuthorizeCode(String code) {
        return code;
    }

    @GetMapping("/google/authorize")
    public String googleAuthorizeCode(String code) {
        return code;
    }
}
