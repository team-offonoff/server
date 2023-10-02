package life.offonoff.ab.api.topic;

import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.api.common.response.PageResponse;
import life.offonoff.ab.service.dto.TopicSearchParams;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.service.topic.TopicService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.*;

@RequiredArgsConstructor
@RequestMapping("/topics")
@RestController
public class TopicController {

    private final TopicService topicService;

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

    @PostMapping("/{topicId}/hide")
    public ResponseEntity<Void> hideTopic(
            @PathVariable("topicId") Long topicId,
            @RequestParam Long memberId // memberId는 임시로 query string
    ) {
        topicService.hide(memberId, topicId);
        return ResponseEntity.ok().build();
    }

    @Getter
    @NoArgsConstructor
    static class TopicInfoResponse {

        private Long topicId;
        private String title;
        private String categoryName;
        private int voteCount;
        private int commentCount;

        public TopicInfoResponse(Topic topic) {
            this.topicId = topic.getId();
            this.title = topic.getTitle();
            this.categoryName = topic.getCategory().getName();
            this.voteCount = topic.getVoteCount();
            this.commentCount = topic.getCommentCount();
        }
    }
}
