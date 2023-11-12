package life.offonoff.ab.application.service.authenticate.oauth;

import life.offonoff.ab.application.service.authenticate.oauth.profile.KakaoProfile;
import life.offonoff.ab.application.service.request.auth.AuthorizeType;
import life.offonoff.ab.application.service.request.auth.KakaoAuthRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static life.offonoff.ab.application.service.request.auth.AuthorizeType.*;
import static life.offonoff.ab.util.jwt.JwtParser.*;

@Component
public class OAuthTemplate {

    private final OAuthRestTemplate oAuthRestTemplate;

    public OAuthTemplate(RestTemplate restTemplate) {
        this.oAuthRestTemplate = new OAuthRestTemplate(restTemplate);
    }

    public KakaoProfile getKakaoProfile(KakaoAuthRequest authRequest) {
        String idToken = getIdToken(authRequest);
        String payload = getPayload(idToken);

        return (KakaoProfile) extractOAuthProfile(payload);
    }

    private String getIdToken(KakaoAuthRequest authRequest) {
        AuthorizeType type = authRequest.getType();

        if (type == BY_CODE) {
            return oAuthRestTemplate.postKakaoToken(authRequest)
                               .getBody()
                               .getIdToken();
        }
        if (type == BY_IDTOKEN) {
            return authRequest.getIdToken();
        }

        return null;
    }

    private String getPayload(String idToken) {
        return idToken.split("\\.")[1];
    }
}
