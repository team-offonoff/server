package life.offonoff.ab.service;

import life.offonoff.ab.domain.category.Category;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.exception.LengthInvalidException;
import life.offonoff.ab.repository.CategoryRepository;
import life.offonoff.ab.service.request.ChoiceCreateRequest;
import life.offonoff.ab.service.request.ImageTextChoiceContentCreateRequest;
import life.offonoff.ab.service.request.TopicCreateRequest;
import life.offonoff.ab.service.request.TopicCreateRequest.TopicCreateRequestBuilder;
import life.offonoff.ab.web.response.TopicResponse;
import lombok.Builder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class TopicServiceTest {
    @Autowired
    TopicService topicService;

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    void TopicCreateRequest_equalOrLessThanMaxLength25_success() {
        assertDoesNotThrow(() -> TopicCreateRequestTestBuilder.builder()
                .topicTitle("엄청길어요엄청길어요엄청길어요엄청길어요엄청길어요")
                .build().createRequest()
        );
    }

    @Test
    void TopicCreateRequest_greaterThanMaxLength25_throwsError() {
        assertThatThrownBy(() -> TopicCreateRequestTestBuilder.builder()
                .topicTitle("엄청길어요엄청길어요엄청길어요엄청길어요엄청길어요엄청길어요")
                .build().createRequest()
        ).isInstanceOf(LengthInvalidException.class);
    }

    @Test
    void createMembersTopic() {
        Category category = createCategory();
        TopicResponse topicResponse = topicService.createMembersTopic(
                0L,
                TopicCreateRequestTestBuilder.builder()
                        .category(category)
                        .build().createRequest());
        System.out.println("topicResponse = " + topicResponse);
    }

    private Category createCategory() {
        Category category = new Category("category");
        categoryRepository.save(category);
        return category;
    }

    @Builder
    public static class TopicCreateRequestTestBuilder {
        private Category category;

        @Builder.Default
        private final TopicCreateRequestBuilder builder = TopicCreateRequest.builder();

        @Builder.Default
        private TopicSide topicSide = TopicSide.TOPIC_A;

        @Builder.Default
        private String topicTitle = "title";

        @Builder.Default
        private List<ChoiceCreateRequest> choices = List.of(
                new ChoiceCreateRequest(
                        ChoiceOption.CHOICE_A,
                        new ImageTextChoiceContentCreateRequest("imageUrl", "choiceA")),
                new ChoiceCreateRequest(
                        ChoiceOption.CHOICE_B,
                        new ImageTextChoiceContentCreateRequest(null, "choiceB"))
        );

        @Builder.Default
        private LocalDateTime deadline = LocalDateTime.now();

        public TopicCreateRequest createRequest() {
            Long categoryId = 0L;
            if (category != null) {
                categoryId = category.getId();
            }
            return builder
                    .topicSide(topicSide)
                    .categoryId(categoryId)
                    .topicTitle(topicTitle)
                    .choices(choices)
                    .deadline(deadline)
                    .build();
        }
    }
}