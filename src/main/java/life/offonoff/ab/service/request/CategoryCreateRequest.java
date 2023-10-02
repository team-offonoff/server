package life.offonoff.ab.service.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
        @NotBlank(message = "카테고리의 이름을 입력해주세요.")
        String name
) {
}
