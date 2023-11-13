package life.offonoff.ab.application.service.request.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class KakaoAuthRequest extends OAuthRequest {
    private AuthorizeType type;

    // token 조회 -> auth
    private String authorizeCode;
    private String redirectUri;

    // auth
    private String idToken;
}
