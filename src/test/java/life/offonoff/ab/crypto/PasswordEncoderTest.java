package life.offonoff.ab.crypto;

import life.offonoff.ab.util.password.BCryptPasswordEncoder;
import life.offonoff.ab.util.password.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class PasswordEncoderTest {

    PasswordEncoder encoder;

    @BeforeEach
    void beforeEach() {
        encoder = new BCryptPasswordEncoder();
    }

    @Test
    void encode_with_same_salt() {
        String origin = "Hello";
        String salt = BCrypt.gensalt();

        String hashpw1 = BCrypt.hashpw(origin, salt);
        String hashpw2 = BCrypt.hashpw(origin, salt);

        log.info("salt : {} / hashpw : {}", salt, hashpw2);
        assertThat(hashpw1).isEqualTo(hashpw2);
    }

    @Test
    void is_match_encoded_with_input() {
        // given
        String origin = "password";
        String encoded = encoder.encode(origin);

        // when
        boolean match = encoder.isMatch(origin, encoded);

        // then
        assertThat(match).isTrue();
    }

}
