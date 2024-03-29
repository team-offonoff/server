package life.offonoff.ab.domain;

import life.offonoff.ab.application.service.common.LengthInfo;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.member.*;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.domain.topic.choice.Choice;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.domain.topic.choice.content.ChoiceContent;
import life.offonoff.ab.domain.vote.Vote;
import lombok.Builder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;

public class TestEntityUtil {

    private static final Random random =  new Random();

    //== Member ==//
    public static Member createRandomMember() {
        String email = generateRandomAlphabetic(20);
        String nickname = generateRandomAlphabetic(LengthInfo.NICKNAME.getMaxLength());
        return createCompletelyJoinedMember(email, "pass", nickname);
    }

    public static String generateRandomAlphabetic(int length) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        return random.ints(leftLimit, rightLimit)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static Member createMember(String email, String password) {
        return new Member(email, password, Provider.NONE);
    }

    public static Member createCompletelyJoinedMember(String email, String password, String nickname) {
        Member member = new Member(email, password, Provider.NONE);

        PersonalInfo personalInfo = new PersonalInfo(nickname, LocalDate.now(), Gender.MALE, "job");
        TermsEnabled termsEnabled = new TermsEnabled(true);

        member.registerPersonalInfo(personalInfo);
        member.agreeTerms(termsEnabled);
        return member;
    }

    //== Topic ==//
    public static Topic createRandomTopic() {
        return createRandomTopicByRandomMember(TopicSide.TOPIC_B);
    }

    public static Topic createRandomTopicHavingChoices(ChoiceOption... options) {
        return createRandomTopicByMemberWithChoices(createRandomMember(), TopicSide.TOPIC_B, options);
    }

    public static Topic createRandomTopicByMemberWithChoices(Member author, TopicSide side, ChoiceOption... options) {
        Topic topic = createRandomTopicByMember(author, side);

        for (var option : options) {
            createChoice(topic, option, null);
        }

        return topic;
    }

    public static Topic createRandomTopicByRandomMember(TopicSide side) {
        return createRandomTopicByMember(createRandomMember(), side);
    }

    public static Topic createRandomTopicByMember(Member author, TopicSide side) {
        String topicTitle = "TITLE_" + generateRandomAlphabetic(10);
        return new Topic(author, topicTitle, side);
    }

    //== Keyword ==//

    public static Keyword createKeyword(int seq) {
        String name = "키워드" + seq; // 6자까지만 가능
        return new Keyword(name, TopicSide.TOPIC_B);
    }
    //== Comment ==//

    public static Comment createComment(int seq) {
        String content = "CONTENT_" + seq;
        return new Comment(content);
    }
    //== Vote ==//

    public static Vote createVote(ChoiceOption option) {
        return new Vote(option, LocalDateTime.now());
    }
    //== Choice ==//

    public static Choice createChoice(Topic topic, ChoiceOption option, ChoiceContent content) {
        return new Choice(topic, option, content);
    }
    //== Pageable ==//
    public static Pageable createPageableDesc(int page, int size, String property) {
        return PageRequest.of(page, size, Sort.Direction.DESC, property);
    }

    public static Pageable createPageableAsc(int page, int size, String property) {
        return PageRequest.of(page, size, Sort.Direction.ASC, property);
    }

    //== Epoch Second ==//
    public static Long getEpochSecond(LocalDateTime from) {
        return from.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    //== Reflection + Builder 패턴으로 엔티티 생성 ==//
    @Builder
    public static class TestMember {
        private Long id;
        private String nickname;
        private LocalDate birth;
        private Gender gender;
        private Role role;
        private String job;
        private String email;
        private String password;

        @Builder.Default
        private NotificationEnabled enabled = new NotificationEnabled(true, true, true, true);

        public Member buildMember() {
            AuthenticationInfo authInfo = new AuthenticationInfo(email, password, Provider.NONE);
            Member member = new Member(authInfo);

            member.registerPersonalInfo(new PersonalInfo(nickname, birth, gender, job));

            ReflectionTestUtils.setField(member, "id", id);
            ReflectionTestUtils.setField(authInfo, "role", role);
            return member;
        }
    }

    @Builder
    public static class TestKeyword {
        private Long id;

        @Builder.Default
        private TopicSide side = TopicSide.TOPIC_B;
        @Builder.Default
        private String name = "key";

        public Keyword buildKeyword() {
            Keyword keyword = new Keyword(name, side);
            ReflectionTestUtils.setField(keyword, "id", id);
            return keyword;
        }
    }

    @Builder
    public static class TestTopic {
        private Long id;
        private TopicSide side;
        private String title;
        private Keyword keyword;
        private int voteCount;

        @Builder.Default
        private Member author = createRandomMember();
        @Builder.Default
        private LocalDateTime deadline = LocalDateTime.now();
        @Builder.Default
        private TopicStatus status = TopicStatus.VOTING;

        public Topic buildTopic() {
            Topic topic = new Topic(author, keyword, title, side, deadline);
            ReflectionTestUtils.setField(topic, "id", id);
            ReflectionTestUtils.setField(topic, "voteCount", voteCount);
            ReflectionTestUtils.setField(topic, "status", status);
            return topic;
        }
    }

    @Builder
    public static class TestComment {
        private Long id;
        private Member writer;
        private Topic topic;
        private ChoiceOption selected;
        private String content;

        public Comment buildComment() {
            Comment comment = new Comment(writer, topic, selected, content);
            ReflectionTestUtils.setField(comment, "id", id);
            return comment;
        }
    }

}
