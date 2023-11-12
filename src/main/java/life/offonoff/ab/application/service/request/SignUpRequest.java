package life.offonoff.ab.application.service.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpRequest {

    private String email;
    private String password;
    private String provider;
}

