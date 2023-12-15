package life.offonoff.ab.application.service.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextUtilsTest {

    @Test
    void lengthOfAnEmoji_equalTo1() {
        String textWithEmoji = "👩‍👩‍👧‍👦안😁녕!hi";

        assertThat(TextUtils.countGraphemeClusters(textWithEmoji))
                .isEqualTo(7);
    }

}