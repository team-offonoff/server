package life.offonoff.ab.service;

import life.offonoff.ab.repository.CategoryRepository;
import life.offonoff.ab.service.request.CategoryCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class CategoryServiceTest {

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    void createCategory_findWithName() {
        CategoryCreateRequest request = new CategoryCreateRequest("name");

        categoryService.createCategory(request);

        assertThat(categoryRepository.findFirstByName("name"))
                .isNotEmpty();
    }





}