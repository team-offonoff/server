package life.offonoff.ab.application.service.request;

import jakarta.validation.constraints.NotNull;

public record VoteCancelRequest(
        @NotNull Long canceledAt
) {
}
