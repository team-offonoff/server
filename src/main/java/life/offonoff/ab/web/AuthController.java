package life.offonoff.ab.web;

import life.offonoff.ab.web.common.aspect.auth.Authorized;
import life.offonoff.ab.application.service.auth.AuthService;
import life.offonoff.ab.application.service.request.auth.SignInRequest;
import life.offonoff.ab.application.service.request.auth.SignUpRequest;
import life.offonoff.ab.web.response.JoinStatusResponse;
import life.offonoff.ab.web.response.SignInResponse;
import life.offonoff.ab.web.response.SignUpResponse;
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
    public ResponseEntity<JoinStatusResponse> getJoinStatus(@Authorized Long memberId) {
        return ResponseEntity.ok(authService.getJoinStatus(memberId));
    }

    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> signIn(@RequestBody SignInRequest request) {
        return ResponseEntity.ok(authService.signIn(request));
    }
}