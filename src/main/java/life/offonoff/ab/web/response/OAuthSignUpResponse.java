package life.offonoff.ab.web.response;

import life.offonoff.ab.domain.member.JoinStatus;

public class OAuthSignUpResponse extends OAuthResponse {

    private final String accessToken;

    public OAuthSignUpResponse(Boolean newMember, Long memberId, JoinStatus joinStatus, String accessToken) {
        super(newMember, memberId, joinStatus);
        this.accessToken = accessToken;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }
}
