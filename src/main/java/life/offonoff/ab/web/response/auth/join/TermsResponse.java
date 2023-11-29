package life.offonoff.ab.web.response.auth.join;

import life.offonoff.ab.domain.member.JoinStatus;
import lombok.Getter;

@Getter
public class TermsResponse extends JoinStatusResponse {

    private String accessToken;

    public TermsResponse(Long memberId, JoinStatus status, String accessToken) {
        super(memberId, status);
        this.accessToken = accessToken;
    }
}
