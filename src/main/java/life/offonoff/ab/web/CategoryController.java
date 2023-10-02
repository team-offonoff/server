package life.offonoff.ab.web;

import jakarta.validation.Valid;
import life.offonoff.ab.service.CategoryService;
import life.offonoff.ab.service.request.CategoryCreateRequest;
import life.offonoff.ab.web.common.AuthenticationId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    /*
     * 현재는 유저가 카테고리를 만들 순 없음. 개발자가 생성해놓은 카테고리만 사용 가능.
     * 후에 유저가 카테고리를 생성하는 기능이 추가될 때
     * 해당 카테고리를 생성한 멤버 정보도 필요하다면 추가 개발 필요.
     */
    @PostMapping
    public ResponseEntity<Void> createCategory(
            @Valid @RequestBody final CategoryCreateRequest request
    ) {
        categoryService.createCategory(request);
        return ResponseEntity.ok().build();
    }
}
