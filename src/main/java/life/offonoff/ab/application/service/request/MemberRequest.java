package life.offonoff.ab.application.service.request;

import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.member.Provider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequest {

    private String email;
    private String password;
    private Provider provider;

    public Member toMember() {
        return new Member(email, password, provider);
    }
}
