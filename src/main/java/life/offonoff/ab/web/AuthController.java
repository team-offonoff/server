package life.offonoff.ab.web;

import life.offonoff.ab.application.service.request.TermsRequest;
import life.offonoff.ab.application.service.request.auth.ProfileRegisterRequest;
import life.offonoff.ab.web.common.aspect.auth.Authorized;
import life.offonoff.ab.application.service.auth.AuthService;
import life.offonoff.ab.application.service.request.auth.SignInRequest;
import life.offonoff.ab.application.service.request.auth.SignUpRequest;
import life.offonoff.ab.web.response.auth.join.JoinStatusResponse;
import life.offonoff.ab.web.response.auth.join.SignUpResponse;
import life.offonoff.ab.web.response.auth.login.SignInResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok(authService.signUp(request));
    }

    /**
     * 회원 가입 진행 상태 확인
     * @param memberId
     * @return
     */
    @GetMapping("/signup/status")
    public ResponseEntity<JoinStatusResponse> getJoinStatus(@Authorized final Long memberId) {
        return ResponseEntity.ok(authService.getJoinStatus(memberId));
    }

    @PostMapping("/signup/profile")
    public ResponseEntity<JoinStatusResponse> registerProfile(
            @RequestBody ProfileRegisterRequest request
    ) {
        return ResponseEntity.ok(authService.registerProfile(request));
    }

    @PostMapping("/signup/terms")
    public ResponseEntity<JoinStatusResponse> enableTerms(
            @RequestBody TermsRequest request
    ) {
        return ResponseEntity.ok(authService.registerTerms(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> signIn(@RequestBody SignInRequest request) {
        return ResponseEntity.ok(authService.signIn(request));
    }

    /**
     * 인증 토큰 (access, refresh) 재발급 API
     */
    @GetMapping("/tokens")
    public ResponseEntity<TokenResponse> getTokens(@RequestBody TokenRequest request) {
        return ResponseEntity.ok(authService.getAuthTokens(request));
    }
}