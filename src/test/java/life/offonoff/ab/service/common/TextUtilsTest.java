package life.offonoff.ab.service.common;

import life.offonoff.ab.application.service.common.TextUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextUtilsTest {

    @Test
    void lengthOfAnEmoji_equalTo1() {
        String textWithEmoji = "👩‍👩‍👧‍👦안😁녕!hi";

        assertThat(TextUtils.getLengthOfEmojiContainableText(textWithEmoji))
                .isEqualTo(7);
    }

}