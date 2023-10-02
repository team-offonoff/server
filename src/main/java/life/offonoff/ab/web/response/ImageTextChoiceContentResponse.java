package life.offonoff.ab.web.response;

import life.offonoff.ab.web.response.ChoiceContentResponseFactory.ChoiceContentResponse;

public record ImageTextChoiceContentResponse(
        String text,
        String imageUrl
) implements ChoiceContentResponse {
}
