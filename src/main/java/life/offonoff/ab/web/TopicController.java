package life.offonoff.ab.web;

import jakarta.validation.Valid;
import life.offonoff.ab.application.service.CommentService;
import life.offonoff.ab.application.service.TopicService;
import life.offonoff.ab.application.service.request.TopicCreateRequest;
import life.offonoff.ab.application.service.request.TopicSearchRequest;
import life.offonoff.ab.application.service.request.VoteModifyRequest;
import life.offonoff.ab.application.service.request.VoteRequest;
import life.offonoff.ab.web.common.aspect.auth.Authorized;
import life.offonoff.ab.web.common.response.PageResponse;
import life.offonoff.ab.web.response.CommentResponse;
import life.offonoff.ab.web.response.VoteResponse;
import life.offonoff.ab.web.response.topic.TopicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.http.ResponseEntity.ok;


@RequiredArgsConstructor
@RestController
@RequestMapping("/topics")
public class TopicController {

    private final TopicService topicService;
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<TopicResponse> createTopic(
            @Authorized Long memberId,
            @Valid @RequestBody final TopicCreateRequest request
    ) {
        return ResponseEntity.ok(topicService.createMembersTopic(memberId, request));
    }
  
    /**
     * 홈 화면 Topic 리스트
     * @param request
     * @param pageable   default 값은 page 0 / size 10 / sort 투표 많은 순(인기 순)
     * @return sliced(paged) Topic 리스트
     */
    @GetMapping("/info")
    public ResponseEntity<PageResponse<TopicResponse>> getTopicInfos(
            @Authorized(nullable = true) Long memberId,
            TopicSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "voteCount", direction = DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(PageResponse.of(topicService.findAll(memberId, request, pageable)));
    }

    /**
     * 단 건 Topic 조회
     */
    @GetMapping("/{topicId}")
    public ResponseEntity<TopicResponse> getTopic(@Authorized(nullable = true) Long memberId,
                                                  @PathVariable Long topicId) {
        return ResponseEntity.ok(topicService.findById(memberId, topicId));
    }

    @PatchMapping("/{topicId}/hide")
    public ResponseEntity<Void> hideTopic(
            @Authorized Long memberId,
            @PathVariable("topicId") Long topicId,
            @RequestParam Boolean hide
    ) {
        topicService.hideTopicForMember(memberId, topicId, hide);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
    }

    @PostMapping("/{topicId}/report")
    public ResponseEntity<Void> reportTopic(
            @Authorized Long memberId,
            @PathVariable("topicId") Long topicId
    ) {
       topicService.reportTopicByMember(topicId, memberId);
        return ok().contentType(MediaType.APPLICATION_JSON).build();
    }

    @PatchMapping("/{topicId}/status")
    public ResponseEntity<Void> activateTopic(
            @Authorized Long memberId,
            @PathVariable("topicId") Long topicId,
            @RequestParam Boolean active
    ) {
        topicService.activateMembersTopic(memberId, topicId, active);
        return ok().contentType(MediaType.APPLICATION_JSON).build();
    }

    @DeleteMapping("/{topicId}")
    public ResponseEntity<Void> deleteTopic(
            @Authorized Long memberId,
            @PathVariable("topicId") Long topicId
    ) {
        topicService.deleteMembersTopic(memberId, topicId);
        return ok().contentType(MediaType.APPLICATION_JSON).build();
    }

    @PostMapping("/{topicId}/vote")
    public ResponseEntity<VoteResponse> voteForTopic(
        @Authorized Long memberId,
        @PathVariable("topicId") Long topicId,
        @Valid @RequestBody final VoteRequest request
    ) {
        return ok(topicService.voteForTopicByMember(topicId, memberId, request));
    }

    @PatchMapping("/{topicId}/vote")
    public ResponseEntity<VoteResponse> modifyVoteForTopic(
            @Authorized Long memberId,
            @PathVariable("topicId") Long topicId,
            @Valid @RequestBody final VoteModifyRequest request
    ) {
        return ok(topicService.modifyVoteForTopicByMember(topicId, memberId, request));
    }

    @GetMapping("/{topicId}/comment")
    public ResponseEntity<CommentResponse> getTopCommentOfTopic(
            @Authorized Long memberId,
            @PathVariable("topicId") Long topicId
    ) {
        return ok(commentService.getLatestCommentOfTopic(topicId));
    }
}
