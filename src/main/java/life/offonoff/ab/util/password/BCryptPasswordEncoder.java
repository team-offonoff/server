package life.offonoff.ab.util.password;

import org.springframework.stereotype.Component;

import static org.springframework.security.crypto.bcrypt.BCrypt.*;

@Component
public class BCryptPasswordEncoder implements PasswordEncoder {

    // hash password with salt
    @Override
    public String encode(String origin) {
        return hashpw(origin, gensalt());
    }

    @Override
    public boolean isMatch(String origin, String encoded) {
        return checkpw(origin, encoded);
    }
}
