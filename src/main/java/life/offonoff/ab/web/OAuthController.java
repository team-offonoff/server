package life.offonoff.ab.web;

import jakarta.validation.Valid;
import life.offonoff.ab.application.service.authenticate.OAuthService;
import life.offonoff.ab.application.service.request.auth.KakaoAuthRequest;
import life.offonoff.ab.web.response.OAuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/oauth")
public class OAuthController {

    private final OAuthService oAuthService;

    // Test Redirect Uri
    @GetMapping("/kakao/authorize/code")
    public String authorizeCode(String code) {
        return code;
    }

    //== OAUTH ==//
    @PostMapping("/kakao/authorize")
    public ResponseEntity<OAuthResponse> authorize(@Valid @RequestBody KakaoAuthRequest request) {
        return ResponseEntity.ok(oAuthService.authenticate(request));
    }
}
