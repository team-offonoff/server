package life.offonoff.ab.application.service.request;

import jakarta.validation.constraints.NotNull;
import life.offonoff.ab.domain.member.TermsEnabled;

public record TermsUpdateRequest(
        boolean marketingTermsEnabled
) {
    public TermsEnabled toTermsEnabled() {
        return new TermsEnabled(marketingTermsEnabled);
    }
}
