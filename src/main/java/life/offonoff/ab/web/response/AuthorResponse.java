package life.offonoff.ab.web.response;

import life.offonoff.ab.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorResponse {

    private Long id;
    private String nickname;

    public static AuthorResponse from(Member member) {
        return new AuthorResponse(member.getId(), member.getNickname());
    }
}
