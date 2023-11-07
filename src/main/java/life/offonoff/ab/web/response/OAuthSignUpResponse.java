package life.offonoff.ab.web.response;

public class OAuthSignUpResponse extends OAuthResponse {

    private final String accessToken;

    public OAuthSignUpResponse(Boolean newMember, String accessToken) {
        super(newMember);
        this.accessToken = accessToken;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }
}
