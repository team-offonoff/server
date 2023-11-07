package life.offonoff.ab.application.authenticate.test;

import life.offonoff.ab.util.jwt.JwtParser;
import life.offonoff.ab.application.service.authenticate.oauth.profile.KakaoProfile;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class JwtParserTest {

    @Test
    void parse_payload() throws IOException {
        // given
        String payload = "eyJhdWQiOiI5MzY0N2E1ZjYwYWEwZjczZDg5YWY0OWY" +
                "3YWUzYWNiYyIsInN1YiI6IjMxNTA0NjM5ODUiLCJhdXRoX3RpbWU" +
                "iOjE2OTkyNTExMDQsImlzcyI6Imh0dHBzOi8va2F1dGgua2FrYW8" +
                "uY29tIiwiZXhwIjoxNjk5MjcyNzA0LCJpYXQiOjE2OTkyNTExMDQ" +
                "sImVtYWlsIjoicnVkd2hkNTE1QGdtYWlsLmNvbSJ9";

        // when
        KakaoProfile profile = (KakaoProfile) JwtParser.extractOAuthProfile(payload);

        // then
        System.out.println(profile.getEmail());
    }
}
