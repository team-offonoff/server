package life.offonoff.ab.util.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import life.offonoff.ab.application.service.auth.oauth.profile.GoogleProfile;
import life.offonoff.ab.application.service.auth.oauth.profile.KakaoProfile;
import life.offonoff.ab.application.service.auth.oauth.profile.OAuthProfile;
import life.offonoff.ab.domain.member.Provider;
import life.offonoff.ab.exception.OAuthMappingException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;

import static life.offonoff.ab.domain.member.Provider.*;

@Component
public class JwtParser {

    private static final ObjectMapper om = new ObjectMapper();
    private static final Base64.Decoder decoder = Base64.getUrlDecoder();

    public OAuthProfile extractOAuthProfile(String payload, Provider provider) {

        Class<? extends OAuthProfile> extractClass = null;

        if (provider == KAKAO) {
            extractClass = KakaoProfile.class;
        }

        if (provider == GOOGLE) {
            extractClass = GoogleProfile.class;
        }

        try {
            return om.readValue(decoder.decode(payload), extractClass);
        } catch (IOException e) {
            throw new OAuthMappingException("OAuth(" + provider.name() + ") 인가 정보를 매핑하는 데에 실패하였습니다.", e);
        }
    }
}
