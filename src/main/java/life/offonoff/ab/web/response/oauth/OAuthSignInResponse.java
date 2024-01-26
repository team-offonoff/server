package life.offonoff.ab.web.response.oauth;

import life.offonoff.ab.domain.member.JoinStatus;
import lombok.Getter;

@Getter
public class OAuthSignInResponse extends OAuthResponse {

    private String accessToken;
    private String refreshToken;

    public OAuthSignInResponse(Boolean newMember, Long memberId, JoinStatus joinStatus, String accessToken, String refreshToken) {
        super(newMember, memberId, joinStatus);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
