package life.offonoff.ab.web;

import jakarta.validation.Valid;
import life.offonoff.ab.application.service.TopicService;
import life.offonoff.ab.application.service.request.TopicCreateRequest;
import life.offonoff.ab.application.service.request.TopicSearchRequest;
import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.web.common.aspect.auth.Authorized;
import life.offonoff.ab.web.common.response.PageResponse;
import life.offonoff.ab.web.response.TopicDetailResponse;
import life.offonoff.ab.web.response.TopicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;


@RequiredArgsConstructor
@RestController
@RequestMapping("/topics")
public class TopicController {
    private final TopicService topicService;

    // TODO: 토픽 보여주기 기능 완료 후 TopicResponse 수정
    @PostMapping
    public ResponseEntity<TopicResponse> createTopic(
            @Authorized Long memberId,
            @Valid @RequestBody final TopicCreateRequest request
    ) {
        return ResponseEntity.ok(
                topicService.createMembersTopic(memberId, request));
    }
  
    /**
     * 홈 화면 Topic 리스트
     * @param request
     * @param pageable   default 값은 page 0 / size 10 / sort 투표 많은 순(인기 순)
     * @return sliced(paged) Topic 리스트
     */
    @GetMapping("/open/now")
    public ResponseEntity<PageResponse<TopicDetailResponse>> getTopicInfos(
            @Authorized Long memberId,
            TopicSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "voteCount", direction = DESC) Pageable pageable
    ) {
        request.setTopicStatus(TopicStatus.VOTING);
        request.setMemberId(memberId);

        return ResponseEntity.ok(PageResponse.of(topicService.searchAll(request, pageable)
                                                             .map(TopicDetailResponse::new))
        );
    }

    @PatchMapping("/{topicId}/hide")
    public ResponseEntity<Void> hideTopic(
            @Authorized Long memberId,
            @PathVariable("topicId") Long topicId,
            @RequestParam Boolean hide
    ) {
        topicService.hideTopicForMember(memberId, topicId, hide);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{topicId}/report")
    public ResponseEntity<Void> reportTopic(
            @Authorized Long memberId,
            @PathVariable("topicId") Long topicId
    ) {
       topicService.reportTopicByMember(topicId, memberId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{topicId}/status")
    public ResponseEntity<Void> activateTopic(
            @Authorized Long memberId,
            @PathVariable("topicId") Long topicId,
            @RequestParam Boolean active
    ) {
        topicService.activateMembersTopic(memberId, topicId, active);
        return ResponseEntity.ok().build();
    }
}
