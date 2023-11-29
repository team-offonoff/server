package life.offonoff.ab.web.response.oauth;

import life.offonoff.ab.domain.member.JoinStatus;
import lombok.Getter;

@Getter
public class OAuthSignInResponse extends OAuthResponse {

    public OAuthSignInResponse(Boolean newMember, Long memberId, JoinStatus joinStatus) {
        super(newMember, memberId, joinStatus);
    }
}
