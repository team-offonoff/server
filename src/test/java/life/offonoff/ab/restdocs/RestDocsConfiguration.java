package life.offonoff.ab.restdocs;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

@TestConfiguration
public class RestDocsConfiguration {
    @Bean
    public RestDocumentationResultHandler write() {
        return MockMvcRestDocumentation.document(
                "{class-name}/{method-name}", // identifier
                preprocessRequest(
                        modifyHeaders()
                                .remove("X-CSRF-TOKEN"), // 테스트에서 post 요청시 사용되는 CSRF 토큰 지우기
                        modifyUris()
                                .host("offonoff.xyz")
                                .removePort(),
                        prettyPrint()),
                preprocessResponse(prettyPrint())
        );
    }
}
