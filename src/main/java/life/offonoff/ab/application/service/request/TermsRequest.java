package life.offonoff.ab.application.service.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import life.offonoff.ab.domain.member.TermsEnabled;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TermsRequest {

    private Long memberId;
    @JsonProperty(value = "listen_marketing")
    private Boolean listenMarketing;

    public TermsEnabled toTermsEnabled() {
        return new TermsEnabled(listenMarketing);
    }
}
