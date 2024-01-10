package life.offonoff.ab.web;

import life.offonoff.ab.application.service.KeywordService;
import life.offonoff.ab.application.service.request.TopicSearchRequest;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.repository.keyword.KeywordRepository;
import life.offonoff.ab.web.common.aspect.auth.Authorized;
import life.offonoff.ab.web.common.response.PageResponse;
import life.offonoff.ab.web.response.KeywordResponse;
import life.offonoff.ab.web.response.topic.TopicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.data.domain.Sort.Direction.ASC;

@RequiredArgsConstructor
@RestController
@RequestMapping("/keywords")
public class KeywordController {

    private final KeywordService keywordService;

    @GetMapping("")
    public ResponseEntity<PageResponse<KeywordResponse>> getKeywords(
            @RequestParam TopicSide side,
            @PageableDefault(page = 0, size = 20, sort = "id", direction = ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(PageResponse.of(keywordService.findAllByTopicSide(side, pageable)));
    }
}
