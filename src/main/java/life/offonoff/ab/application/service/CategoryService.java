package life.offonoff.ab.application.service;

import life.offonoff.ab.domain.category.Category;
import life.offonoff.ab.repository.CategoryRepository;
import life.offonoff.ab.application.service.request.CategoryCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public void createCategory(final CategoryCreateRequest request) {
        Category category = new Category(request.name());
        categoryRepository.save(category);
    }

}
