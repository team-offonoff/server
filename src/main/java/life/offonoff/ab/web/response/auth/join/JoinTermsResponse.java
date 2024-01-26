package life.offonoff.ab.web.response.auth.join;

import life.offonoff.ab.domain.member.JoinStatus;
import lombok.Getter;

@Getter
public class JoinTermsResponse extends JoinStatusResponse {

    private String accessToken;
    private String refreshToken;

    public JoinTermsResponse(Long memberId, JoinStatus status, String accessToken, String refreshToken) {
        super(memberId, status);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
