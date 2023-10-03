package life.offonoff.ab.service.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {
    private static final Pattern graphemePattern = Pattern.compile("\\X");
    private static final Matcher graphemeMatcher = graphemePattern.matcher("");

    /*
     * 이모티콘이 포함된 문자, 즉 2byte가 넘는 문자가 있을 경우 String의 길이는 우리가 인식하는 글자 단위보다 길어진다.
     * 이 함수는 우리가 인식하는 대로 길이를 읽어온다.
     */
    public static int getLengthOfEmojiContainableText(String text) {
        if (text == null) {
            return 0;
        }
        graphemeMatcher.reset(text);
        int count = 0;
        while (graphemeMatcher.find()) {
            count++;
        }
        return count;
    }
}
