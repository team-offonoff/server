package life.offonoff.ab.web.response.auth.join;

import life.offonoff.ab.domain.member.JoinStatus;

public class ProfileRegisterResponse extends JoinStatusResponse {

    public ProfileRegisterResponse(Long memberId, JoinStatus status) {
        super(memberId, status);
    }
}
