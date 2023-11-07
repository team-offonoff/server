package life.offonoff.ab.web.response;

public abstract class OAuthResponse {

    private final Boolean newMember;

    public OAuthResponse(Boolean newMember) {
        this.newMember = newMember;
    }

    public Boolean isNewMember() {
        return newMember;
    }

    public abstract String getAccessToken();
}
