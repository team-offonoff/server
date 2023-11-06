package life.offonoff.ab.application.service;

import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.repository.CategoryRepository;
import life.offonoff.ab.application.service.request.CategoryCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryServiceTest {

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    void createCategory_findWithName() {
        CategoryCreateRequest request = new CategoryCreateRequest("name", TopicSide.TOPIC_A);

        categoryService.createCategory(request);

        assertThat(categoryRepository.findFirstByName("name"))
                .isNotEmpty();
    }





}