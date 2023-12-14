package life.offonoff.ab.application.service;

import life.offonoff.ab.exception.IllegalImageExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class S3ServiceTest {
    private S3Service s3Service;
    private final String profileImageName = "profileImage";
    private final String baseDir = "baseDir";

    @BeforeEach
    void setUp() {
        // key가 유효하지 않아도 url은 발급됨
        s3Service = new S3Service("dd", "dd", "bucket", profileImageName, baseDir);
    }

    @Test
    void getImageFileExtension_withImageFileExtension() {
        String targetMethod = "getImageFileExtension";

        String fileExtension = ReflectionTestUtils.invokeMethod(s3Service, targetMethod, "image.png");

        assertThat(fileExtension).isEqualTo("png");
    }

    @Test
    void getImageFileExtension_withNonImageFileExtension() {
        String targetMethod = "getImageFileExtension";

        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(s3Service, targetMethod, "image.txt"))
                .isInstanceOf(IllegalImageExtension.class);
    }

    @Test
    void getImageFileExtension_withNoFileExtension() {
        String targetMethod = "getImageFileExtension";

        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(s3Service, targetMethod, "image"))
                .isInstanceOf(IllegalImageExtension.class);
    }

    @Test
    void convertToImageContentType_withImageFileExtension() {
        String targetMethod = "convertToImageContentType";

        String fileExtension = ReflectionTestUtils.invokeMethod(s3Service, targetMethod, "png");

        assertThat(fileExtension).isEqualTo("image/png");
    }

    @Test
    void createFileName() {
        String targetMethod = "createFileName";

        String fileName = ReflectionTestUtils.invokeMethod(s3Service, targetMethod, "png");

        assertThat(fileName.endsWith(".png")).isTrue();
    }

    @Test
    void generateImageKeyName() {
        String targetMethod = "generateImageKeyName";

        String fileExtension = ReflectionTestUtils.invokeMethod(s3Service, targetMethod, 1L, "topics/test.png");

        assertThat(fileExtension).isEqualTo(baseDir + "/1/topics/test.png");
    }

    @Test
    void createMembersProfileImageSignedUrl() {
        String url = s3Service.createMembersProfileImageSignedUrl(1L, "image.jpeg");

        assertThat(url.startsWith("https://bucket.s3.ap-northeast-2.amazonaws.com/"+baseDir+"/1/profile/")).isTrue();
    }

    @Test
    void createMembersTopicImageSignedUrl() {
        String url = s3Service.createMembersTopicImageSignedUrl(1L, "image.jpeg");

        assertThat(url.startsWith("https://bucket.s3.ap-northeast-2.amazonaws.com/"+baseDir+"/1/topics/")).isTrue();
    }
}