package life.offonoff.ab.web.response;

import lombok.Getter;

@Getter
public class OAuthSignInResponse extends OAuthResponse {

    private final String accessToken;

    public OAuthSignInResponse(Boolean newMember, String accessToken) {
        super(newMember);
        this.accessToken = accessToken;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }
}
