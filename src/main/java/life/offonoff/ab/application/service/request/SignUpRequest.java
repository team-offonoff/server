package life.offonoff.ab.application.service.request;

import life.offonoff.ab.domain.member.Provider;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpRequest {

    private String email;
    private String password;
    private Provider provider;
}

