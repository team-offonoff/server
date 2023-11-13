package life.offonoff.ab.application.service.authenticate.oauth;

import life.offonoff.ab.application.service.authenticate.oauth.token.OAuthTokenResponse;
import life.offonoff.ab.application.service.request.auth.OAuthRequest;
import life.offonoff.ab.domain.member.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static life.offonoff.ab.application.service.authenticate.oauth.OAuthRequestConst.*;

@RequiredArgsConstructor
public class OAuthRestTemplate {

    private final RestTemplate restTemplate;
    private final OAuthRestRequestBuilder oAuthRestRequestBuilder;

    public ResponseEntity<OAuthTokenResponse> postOAuthToken(final OAuthRequest request) {

        final String authorizeCode = request.getAuthorizeCode();
        final String redirectUri = request.getRedirectUri();
        final Provider provider = request.getProvider();

        return restTemplate.postForEntity(
                findPostTokenUrlOf(provider),
                oAuthRestRequestBuilder.buildOAuthTokenRequest(authorizeCode, redirectUri, provider),
                OAuthTokenResponse.class);
    }

}
