package life.offonoff.ab.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import life.offonoff.ab.application.service.S3Service;
import life.offonoff.ab.config.WebConfig;
import life.offonoff.ab.exception.IllegalImageExtension;
import life.offonoff.ab.restdocs.RestDocsTest;
import life.offonoff.ab.web.ImageController.ImageRequest;
import life.offonoff.ab.web.common.aspect.auth.AuthorizedArgumentResolver;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ImageController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuthorizedArgumentResolver.class)
        })
public class ImageControllerTest extends RestDocsTest {

    @MockBean
    S3Service s3Service;

    @Test
    void createProfileImageUrl() throws Exception {
        ImageRequest request = new ImageRequest("image.png");
        String url = "https://bucket.s3.ap-northeast-2.amazonaws.com/baseDir/1/profile/profile.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20231214T050129Z&X-Amz-SignedHeaders=content-type%3Bhost&X-Amz-Credential=dd%2F20231214%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=600&X-Amz-Signature=1ad8851f1779015fe8e4e96e0279bcd04df97e80c90ec29e91613126502568d4";
        when(s3Service.createMembersProfileImageSignedUrl(any(), any()))
                .thenReturn(url);

        mvc.perform(post(ImageUri.PROFILE).with(csrf().asHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("presignedUrl").value(url));
    }

    @Test
    void createProfileImageUrl_withUnsupportedFileExtension() throws Exception {
        doThrow(IllegalImageExtension.class).when(s3Service).createMembersProfileImageSignedUrl(any(), any());

        mvc.perform(post(ImageUri.PROFILE).with(csrf().asHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(new ImageRequest("image.txt"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("abCode").value("ILLEGAL_FILE_EXTENSION"));
    }

    @Test
    void createTopicImageUrl() throws Exception {
        ImageRequest request = new ImageRequest("image.png");
        String url = "https://bucket.s3.ap-northeast-2.amazonaws.com/baseDir/1/topics/90c51c5a-1764-4ace-90b4-a1fda4937cca.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20231214T051248Z&X-Amz-SignedHeaders=content-type%3Bhost&X-Amz-Credential=dd%2F20231214%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=600&X-Amz-Signature=079c040627808f07bd9060b90b572cea7d772e08f8ab8810212b71cb51705a1f";
        when(s3Service.createMembersTopicImageSignedUrl(any(), any()))
                .thenReturn(url);

        mvc.perform(post(ImageUri.TOPIC).with(csrf().asHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("presignedUrl").value(url));
    }

    private static class ImageUri {
        private static final String BASE = "/images";
        private static final String PROFILE = BASE + "/profile";
        private static final String TOPIC = BASE + "/topic";
    }
}
