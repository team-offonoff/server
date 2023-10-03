package life.offonoff.ab.web;

import java.time.LocalDateTime;

import static org.springframework.data.domain.Sort.Direction.*;

import jakarta.validation.Valid;
import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.web.common.response.PageResponse;
import life.offonoff.ab.service.dto.TopicSearchParams;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.service.TopicService;
import life.offonoff.ab.service.request.TopicCreateRequest;
import life.offonoff.ab.web.common.AuthenticationId;
import life.offonoff.ab.web.response.TopicResponse;
import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


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
  
    /**
     * 홈 화면 Topic 리스트
     * @param categoryId
     * @param pageable   default 값은 page 0 / size 10 / sort 투표 많은 순(인기 순)
     * @return sliced(paged) Topic 리스트
     */
    @GetMapping("/now")
    public ResponseEntity<PageResponse<TopicInfoResponse>> getTopicInfos(
            @RequestParam Long memberId,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(page = 0, size = 10, sort = "voteCount", direction = DESC) Pageable pageable
    ) {
        TopicSearchParams params = new TopicSearchParams(TopicStatus.VOTING, memberId, categoryId);

        return ResponseEntity.ok(PageResponse.of(topicService.searchAllNotHidden(params, pageable)
                                                             .map(TopicInfoResponse::new))
        );
    }

    @PatchMapping("/{topicId}/hide")
    public ResponseEntity<Void> hideTopic(
            @PathVariable("topicId") Long topicId,
            @RequestParam Boolean hide,
            @RequestParam Long memberId // memberId는 임시로 query string
    ) {
        topicService.hide(memberId, topicId, hide);
        return ResponseEntity.ok().build();
    }

    @Getter
    @NoArgsConstructor
    static class TopicInfoResponse {

        private Long topicId;
        private String title;
        private Long publishMemberId;
        private String publishMemberNickname;
        private LocalDateTime deadline;
        private int voteCount;
        private int commentCount;

        public TopicInfoResponse(Topic topic) {
            this.topicId = topic.getId();
            this.title = topic.getTitle();
            this.publishMemberId = topic.getPublishMember().getId();
            this.publishMemberNickname = topic.getPublishMember().getNickname();
            this.deadline = topic.getDeadline();
            this.voteCount = topic.getVoteCount();
            this.commentCount = topic.getCommentCount();
        }
    }
}
