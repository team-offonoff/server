package life.offonoff.ab.crypto;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class PasswordEncoder {

    @Test
    void encode_with_same_salt() {
        String origin = "Hello";
        String salt = BCrypt.gensalt();

        String hashpw1 = BCrypt.hashpw(origin, salt);
        String hashpw2 = BCrypt.hashpw(origin, salt);

        log.info("salt : {} / hashpw : {}", salt, hashpw2);
        assertThat(hashpw1).isEqualTo(hashpw2);
    }

}
