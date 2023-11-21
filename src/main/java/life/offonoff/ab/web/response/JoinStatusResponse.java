package life.offonoff.ab.web.response;

import life.offonoff.ab.domain.member.JoinStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JoinStatusResponse {

    private Long memberId;
    private JoinStatus status;
}
