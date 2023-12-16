package life.offonoff.ab.application.service.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {
    private static final Pattern graphemePattern = Pattern.compile("\\X");

    /*
     * 이모티콘이 포함된 문자와 같이  2byte가 넘는 문자가 있을 경우 String의 길이는 우리가 인식하는 글자 단위보다 길어진다.
     * 이 함수는 우리가 인식하는 대로 길이를 읽어온다.
     *
     * Grapheme Cluster: 우리가 인식하는 글자 단위
     */
    public static int countGraphemeClusters(String text) {
        if (text == null) {
            return 0;
        }
        final Matcher graphemeMatcher = graphemePattern.matcher(text);
        return (int) graphemeMatcher.results().count();
    }

    public static int countEmojis(String text) {
        if (text == null) {
            return 0;
        }
        final Matcher graphemeMatcher = graphemePattern.matcher(text);
        return (int) graphemeMatcher.results()
                .filter(result -> result.group().length() > 1)
                .count();
    }

    /*
     * 디자인상 이모티콘이 일반 문자보다 영역을 많이 차지하므로, 이모티콘은 길이를 2로 처리하는 경우 사용
     */
    public static int countGraphemeClustersWithLongerEmoji(String text) {
        return countGraphemeClusters(text) + countEmojis(text);
    }
}
