package life.offonoff.ab.web.response;

import life.offonoff.ab.domain.member.JoinStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInResponse {

    private Long memberId;
    private JoinStatus joinStatus;
    private String accessToken;
}
