package life.offonoff.ab.application.service.authenticate.oauth;

import life.offonoff.ab.application.service.authenticate.oauth.profile.OAuthProfile;
import life.offonoff.ab.application.service.authenticate.oauth.rest.OAuthRestRequestBuilder;
import life.offonoff.ab.application.service.authenticate.oauth.rest.OAuthRestTemplate;
import life.offonoff.ab.application.service.request.auth.AuthorizeType;
import life.offonoff.ab.application.service.request.auth.OAuthRequest;
import life.offonoff.ab.util.jwt.JwtParser;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static life.offonoff.ab.application.service.request.auth.AuthorizeType.*;

@Component
public class OAuthTemplate {

    private final JwtParser jwtParser;
    private final OAuthRestTemplate oAuthRestTemplate;

    public OAuthTemplate(JwtParser jwtParser, RestTemplate restTemplate, OAuthRestRequestBuilder oAuthRestRequestBuilder) {
        this.jwtParser = jwtParser;
        this.oAuthRestTemplate = new OAuthRestTemplate(restTemplate, oAuthRestRequestBuilder);
    }

    public OAuthProfile getOAuthProfile(OAuthRequest request) {
        String idToken = getIdToken(request);
        String payload = getPayload(idToken);

        return jwtParser.extractOAuthProfile(payload, request.getProvider());
    }

    private String getIdToken(OAuthRequest request) {
        AuthorizeType type = request.getType();

        if (type == BY_CODE) {
            return oAuthRestTemplate.postOAuthToken(request)
                                    .getBody()
                                    .getIdToken();
        }
        if (type == BY_IDTOKEN) {
            return request.getIdToken();
        }

        return null;
    }

    private String getPayload(String idToken) {
        return idToken.split("\\.")[1];
    }
}
