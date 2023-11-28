package life.offonoff.ab.web.response.auth.join;

import life.offonoff.ab.domain.member.JoinStatus;

public class SignUpResponse extends JoinStatusResponse {

    public SignUpResponse(Long memberId, JoinStatus joinStatus) {
        super(memberId, joinStatus);
    }
}
