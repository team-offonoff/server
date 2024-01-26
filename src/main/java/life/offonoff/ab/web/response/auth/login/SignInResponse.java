package life.offonoff.ab.web.response.auth.login;

import life.offonoff.ab.domain.member.JoinStatus;
import life.offonoff.ab.web.response.auth.join.JoinStatusResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInResponse {

    private Long memberId;
    private JoinStatus joinStatus;
    private String accessToken;
    private String refreshToken;
}
