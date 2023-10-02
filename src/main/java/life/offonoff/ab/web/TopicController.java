package life.offonoff.ab.web;

import jakarta.validation.Valid;
import life.offonoff.ab.service.TopicService;
import life.offonoff.ab.service.request.TopicCreateRequest;
import life.offonoff.ab.web.common.AuthenticationId;
import life.offonoff.ab.web.response.TopicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/topics")
public class TopicController {
    private final TopicService topicService;

    // TODO: 토픽 보여주기 기능 완료 후 TopicResponse 수정
    @PostMapping
    public ResponseEntity<TopicResponse> createCategory(
            @AuthenticationId final Long memberId,
            @Valid @RequestBody final TopicCreateRequest request
    ) {
        return ResponseEntity.ok(
                topicService.createMembersTopic(memberId, request));
    }
}
