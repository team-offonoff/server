package life.offonoff.ab.web;

import life.offonoff.ab.application.service.S3Service;
import life.offonoff.ab.web.common.aspect.auth.Authorized;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/images")
@RestController
public class ImageController {
    private final S3Service s3Service;

    @PostMapping("/profile")
    public ResponseEntity<ImageResponse> createMembersProfileImageSignedUrl(
            @Authorized final Long memberId,
            @RequestBody final ImageRequest request
    ) {
        String url = s3Service.createMembersProfileImageSignedUrl(memberId, request.fileName);
        return ResponseEntity.ok(new ImageResponse(url));
    }

    @PostMapping("/topic")
    public ResponseEntity<ImageResponse> createMembersTopicImageSignedUrl(
            @Authorized final Long memberId,
            @RequestBody final ImageRequest request
    ) {
        String url = s3Service.createMembersTopicImageSignedUrl(memberId, request.fileName);
        return ResponseEntity.ok(new ImageResponse(url));
    }

    public record ImageRequest(String fileName) {}
    public record ImageResponse(String presignedUrl) {}
}
