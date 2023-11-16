package life.offonoff.ab.application.service.request.auth;

import life.offonoff.ab.domain.member.Provider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    private String email;
    private String password;
    private Provider provider = Provider.NONE;

    public void setEncodedPassword(String encoded) {
        this.password = encoded;
    }
}

