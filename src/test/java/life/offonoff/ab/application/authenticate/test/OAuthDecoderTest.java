package life.offonoff.ab.application.authenticate.test;

import life.offonoff.ab.domain.member.Provider;
import life.offonoff.ab.util.token.OAuthDecoder;
import life.offonoff.ab.application.service.auth.oauth.profile.KakaoProfile;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class OAuthDecoderTest {

    OAuthDecoder parser = new OAuthDecoder();

    @Test
    void parse_payload() throws IOException {
        // given
        String payload = "eyJhdWQiOiI5MzY0N2E1ZjYwYWEwZjczZDg5YWY0OWY" +
                "3YWUzYWNiYyIsInN1YiI6IjMxNTA0NjM5ODUiLCJhdXRoX3RpbWU" +
                "iOjE2OTkyNTExMDQsImlzcyI6Imh0dHBzOi8va2F1dGgua2FrYW8" +
                "uY29tIiwiZXhwIjoxNjk5MjcyNzA0LCJpYXQiOjE2OTkyNTExMDQ" +
                "sImVtYWlsIjoicnVkd2hkNTE1QGdtYWlsLmNvbSJ9";

        // when
        KakaoProfile profile = (KakaoProfile) parser.extractOAuthProfile(payload, Provider.KAKAO);

        // then
        System.out.println(profile.getEmail());
    }
}
