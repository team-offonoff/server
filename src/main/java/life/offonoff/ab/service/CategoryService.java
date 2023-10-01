package life.offonoff.ab.service;

import life.offonoff.ab.domain.category.Category;
import life.offonoff.ab.repository.CategoryRepository;
import life.offonoff.ab.service.request.CategoryCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public void createCategory(final CategoryCreateRequest request) {
        Category category = new Category(request.name());
        categoryRepository.save(category);
    }

}
