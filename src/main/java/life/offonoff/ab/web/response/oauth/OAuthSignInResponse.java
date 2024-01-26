package life.offonoff.ab.web.response.oauth;

import life.offonoff.ab.domain.member.JoinStatus;
import lombok.Getter;

@Getter
public class OAuthSignInResponse extends OAuthResponse {

    private String accessToken;

    public OAuthSignInResponse(Boolean newMember, Long memberId, JoinStatus joinStatus, String accessToken) {
        super(newMember, memberId, joinStatus);
        this.accessToken = accessToken;
    }
}
