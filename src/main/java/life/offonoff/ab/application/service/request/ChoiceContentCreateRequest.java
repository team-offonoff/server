package life.offonoff.ab.application.service.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import life.offonoff.ab.domain.topic.choice.content.ChoiceContent;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImageTextChoiceContentCreateRequest.class, name = "IMAGE_TEXT")
})
public interface ChoiceContentCreateRequest {
    ChoiceContent toEntity();
}
