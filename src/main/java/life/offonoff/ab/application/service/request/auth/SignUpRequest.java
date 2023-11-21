package life.offonoff.ab.application.service.request.auth;

import life.offonoff.ab.application.service.request.MemberRequest;
import life.offonoff.ab.domain.member.Provider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequest extends MemberRequest {

    public SignUpRequest(final String email, final String password, final Provider provider) {
        super(email, password, provider);
    }

    public void setEncodedPassword(final String encoded) {
        super.setPassword(encoded);
    }
}

