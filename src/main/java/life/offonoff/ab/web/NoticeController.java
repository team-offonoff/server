package life.offonoff.ab.web;

import life.offonoff.ab.application.notice.NoticeService;
import life.offonoff.ab.web.common.aspect.auth.Authorized;
import life.offonoff.ab.web.response.notice.NoticeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("")
    public ResponseEntity<List<NoticeResponse>> getNotices(@Authorized Long memberId) {
        return ResponseEntity.ok(noticeService.findAllByReceiverId(memberId));
    }
}
