package life.offonoff.ab.application.service.auth.oauth;

import life.offonoff.ab.application.service.auth.oauth.profile.OAuthProfile;
import life.offonoff.ab.application.service.auth.oauth.rest.OAuthRestRequestBuilder;
import life.offonoff.ab.application.service.auth.oauth.rest.OAuthRestTemplate;
import life.offonoff.ab.application.service.request.oauth.AuthorizeType;
import life.offonoff.ab.application.service.request.oauth.OAuthRequest;
import life.offonoff.ab.util.token.OAuthDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static life.offonoff.ab.application.service.request.oauth.AuthorizeType.*;

@Component
public class OAuthTemplate {

    private final OAuthDecoder decoder;
    private final OAuthRestTemplate oAuthRestTemplate;

    public OAuthTemplate(OAuthDecoder decoder, RestTemplate restTemplate, OAuthRestRequestBuilder oAuthRestRequestBuilder) {
        this.decoder = decoder;
        this.oAuthRestTemplate = new OAuthRestTemplate(restTemplate, oAuthRestRequestBuilder);
    }

    public OAuthProfile getOAuthProfile(OAuthRequest request) {
        String idToken = getIdToken(request);
        String payload = getPayload(idToken);

        return decoder.extractOAuthProfile(payload, request.getProvider());
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
