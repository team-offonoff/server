package life.offonoff.ab.web.response;

import life.offonoff.ab.domain.member.JoinStatus;
import lombok.Getter;

@Getter
public class OAuthSignInResponse extends OAuthResponse {

    private final String accessToken;

    public OAuthSignInResponse(Boolean newMember, Long memberId, JoinStatus joinStatus, String accessToken) {
        super(newMember, memberId, joinStatus);
        this.accessToken = accessToken;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }
}
