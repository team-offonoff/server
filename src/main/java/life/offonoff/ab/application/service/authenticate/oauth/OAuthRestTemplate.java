package life.offonoff.ab.application.service.authenticate.oauth;

import life.offonoff.ab.application.service.request.auth.KakaoAuthRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static life.offonoff.ab.application.service.authenticate.oauth.OAuthRequestConst.POST_KAKAO_TOKEN_URL;

@RequiredArgsConstructor
public class OAuthRestTemplate {

    private final RestTemplate restTemplate;

    @Value("${ab.auth.oauth.kakao.client-id}")
    private static String KAKAO_CLIENT_ID;

    //== KAKAO ==//
    public ResponseEntity<KakaoTokenResponse> postKakaoToken(final KakaoAuthRequest request) {

        final String authorizeCode = request.getAuthorizeCode();
        final String redirectUri = request.getRedirectUri();

        return restTemplate.postForEntity(POST_KAKAO_TOKEN_URL,
                                          createRequest(authorizeCode, redirectUri),
                                          KakaoTokenResponse.class);
    }

    private HttpEntity<MultiValueMap<String, Object>> createRequest(String authorizeCode, String redirectUri) {
        // Set Headers
        HttpHeaders headers = getHeaders();
        // Set Parameters
        MultiValueMap<String, Object> params = getMultiValueMap(authorizeCode, redirectUri);

        // Set HttpEntity
        return new HttpEntity<>(params, headers);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private MultiValueMap<String, Object> getMultiValueMap(String authorizeCode, String redirectUri) {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("client_id", "93647a5f60aa0f73d89af49f7ae3acbc");
        map.add("code", authorizeCode);
        map.add("redirect_uri", redirectUri);
        return map;
    }

}
