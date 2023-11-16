package life.offonoff.ab.web.response;

import life.offonoff.ab.domain.member.JoinStatus;
import lombok.Getter;

@Getter
public abstract class OAuthResponse {

    private final Boolean newMember;
    private final Long memberId;
    private final JoinStatus joinStatus;

    public OAuthResponse(Boolean newMember, Long memberId, JoinStatus joinStatus) {
        this.newMember = newMember;
        this.memberId = memberId;
        this.joinStatus = joinStatus;
    }

    public abstract String getAccessToken();
}
