package life.offonoff.ab.web;

import lombok.*;

@Getter
@Builder
public class TokenResponse {

    private Long memberId;
    private String accessToken;
    private String refreshToken;
}
