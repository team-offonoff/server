package life.offonoff.ab.web.response;

import life.offonoff.ab.domain.member.Member;
import lombok.Getter;

@Getter
public class MemberInfoResponse {
    private Long id;
    private String nickname;

    public MemberInfoResponse(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    public static MemberInfoResponse of(Member member) {
        return new MemberInfoResponse(member.getId(), member.getNickname());
    }
}
