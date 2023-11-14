package life.offonoff.ab.application.service.auth.oauth.rest;

import life.offonoff.ab.domain.member.Provider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static life.offonoff.ab.domain.member.Provider.*;

@Component
public class OAuthRestRequestBuilder {

    @Value("${ab.auth.oauth.kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${ab.auth.oauth.google.client-id}")
    private String GOOGLE_CLIENT_ID;
    @Value("${ab.auth.oauth.google.client-secret}")
    private String GOOGLE_CLIENT_SECRET;

    public HttpEntity<MultiValueMap<String, Object>> buildOAuthTokenRequest(
            final String authorizeCode,
            final String redirectUri,
            final Provider provider
    ) {
        // Set Headers
        HttpHeaders headers = getHeaders();

        // Set Parameters
        MultiValueMap<String, Object> params = getMultiValueMap(authorizeCode, redirectUri, provider);

        // Set HttpEntity
        return new HttpEntity<>(params, headers);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private MultiValueMap<String, Object> getMultiValueMap(
            final String authorizeCode,
            final String redirectUri,
            final Provider provider
    ) {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("code", authorizeCode);
        map.add("redirect_uri", redirectUri);

        if (provider == KAKAO) {
            map.add("client_id", KAKAO_CLIENT_ID);
            return map;
        }

        if (provider == GOOGLE) {
            map.add("client_id", GOOGLE_CLIENT_ID);
            map.add("client_secret", GOOGLE_CLIENT_SECRET);
            return map;
        }

        throw new RuntimeException();
    }
}

