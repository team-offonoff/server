package life.offonoff.ab.web.response;

import life.offonoff.ab.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponse {

    private Long id;
    private String nickname;
    private String profileImageURl;

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getNickname(), member.getProfileImageUrl());
    }
}
