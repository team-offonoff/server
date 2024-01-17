package life.offonoff.ab.web;

import life.offonoff.ab.application.service.CommentService;
import life.offonoff.ab.application.service.request.CommentRequest;
import life.offonoff.ab.application.service.request.CommentUpdateRequest;
import life.offonoff.ab.web.common.aspect.auth.Authorized;
import life.offonoff.ab.web.common.response.PageResponse;
import life.offonoff.ab.web.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RequiredArgsConstructor
@RequestMapping("/comments")
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @Authorized Long memberId,
            @RequestBody CommentRequest request
    ) {
        return ResponseEntity.ok(commentService.register(memberId, request));
    }

    @GetMapping("") // TODO : 댓글 조회 시에 멤버가 좋아요/싫어요 누른 댓글인지 정보 추가
    public ResponseEntity<PageResponse<CommentResponse>> getComments(
            @Authorized Long memberId,
            @RequestParam("topic-id") Long topicId,
            @PageableDefault(page = 0, size = 50, sort = "createdAt", direction = DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(commentService.findAll(memberId, topicId, pageable));
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<Void> likeComment(
            @Authorized Long memberId,
            @PathVariable Long commentId,
            @RequestParam Boolean enable
    ) {
        commentService.likeCommentForMember(memberId, commentId, enable);
        return ResponseEntity.ok()
                             .build();
    }

    @PostMapping("/{commentId}/hate")
    public ResponseEntity<Void> hateComment(
            @Authorized Long memberId,
            @PathVariable Long commentId,
            @RequestParam Boolean enable
    ) {
        commentService.hateCommentForMember(memberId, commentId, enable);
        return ResponseEntity.ok()
                             .build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@Authorized Long memberId, @PathVariable Long commentId) {
        commentService.deleteComment(memberId, commentId);
        return ResponseEntity.ok()
                             .build();
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> modifyComment(
            @Authorized Long memberId,
            @PathVariable Long commentId,
            @RequestBody CommentUpdateRequest request
    ) {
        return ResponseEntity.ok(commentService.modifyMembersCommentContent(memberId, commentId, request.content()));
    }
}
