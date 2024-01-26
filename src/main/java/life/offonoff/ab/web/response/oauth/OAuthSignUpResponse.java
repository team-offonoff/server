package life.offonoff.ab.web.response.oauth;

import life.offonoff.ab.domain.member.JoinStatus;
import lombok.Getter;

@Getter
public class OAuthSignUpResponse extends OAuthResponse {

    public OAuthSignUpResponse(Boolean newMember, Long memberId, JoinStatus joinStatus) {
        super(newMember, memberId, joinStatus);
    }
}
