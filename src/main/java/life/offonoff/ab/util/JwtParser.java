package life.offonoff.ab.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import life.offonoff.ab.application.service.authenticate.oauth.profile.KakaoProfile;
import life.offonoff.ab.application.service.authenticate.oauth.profile.OAuthProfile;
import life.offonoff.ab.exception.OAuthMappingException;

import java.io.IOException;
import java.util.Base64;

public class JwtParser {

    private static final ObjectMapper om = new ObjectMapper();
    private static final Base64.Decoder decoder = Base64.getUrlDecoder();

    public static OAuthProfile extractOAuthProfile(String payload) {
        try {
            return om.readValue(decoder.decode(payload), KakaoProfile.class);
        } catch (IOException e) {
            throw new OAuthMappingException("KAKAO 인가 정보를 매핑하는 데에 실패하였습니다.", e);
        }
    }
}
