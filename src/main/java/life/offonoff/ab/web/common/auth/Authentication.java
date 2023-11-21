package life.offonoff.ab.web.common.auth;

import life.offonoff.ab.domain.member.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Authentication {

    private Long memberId;
    private Role role;

}
