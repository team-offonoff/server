package life.offonoff.ab.web.response;

import life.offonoff.ab.domain.member.TermsEnabled;

public record TermsResponse(
        boolean marketingTermsEnabled
) {
    public static TermsResponse from(TermsEnabled termsEnabled) {
        return new TermsResponse(termsEnabled.getListenMarketing());
    }
}
