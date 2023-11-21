package life.offonoff.ab.util.token;

import io.jsonwebtoken.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class JwtProviderTest {

    @Autowired JwtProvider jwtProvider;

    @Test
    @DisplayName("정상 서명")
    void generate_and_parse_member_id() {
        Long memberId = 1L;

        String accessToken = jwtProvider.generateAccessToken(memberId);

        assertThat(jwtProvider.parseMemberId(accessToken)).isEqualTo(memberId);
    }
}