package life.offonoff.ab.application.service.authenticate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpRequest {

    private String email;
    private String password;
}

