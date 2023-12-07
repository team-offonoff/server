package life.offonoff.ab.application.service;

import life.offonoff.ab.application.event.topic.TopicCreateEvent;
import life.offonoff.ab.application.service.request.*;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.domain.vote.Vote;
import life.offonoff.ab.exception.LengthInvalidException;
import life.offonoff.ab.repository.KeywordRepository;
import life.offonoff.ab.repository.ChoiceRepository;
import life.offonoff.ab.repository.VoteRepository;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.repository.topic.TopicRepository;
import life.offonoff.ab.web.response.topic.choice.ChoiceResponse;
import life.offonoff.ab.web.response.topic.choice.content.ImageTextChoiceContentResponse;
import life.offonoff.ab.web.response.KeywordResponse;
import life.offonoff.ab.web.response.topic.TopicResponse;
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static life.offonoff.ab.domain.TestEntityUtil.TestKeyword;
import static life.offonoff.ab.domain.TestEntityUtil.TestMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class TopicServiceTest {

    @InjectMocks
    TopicService topicService;

    @Mock
    KeywordRepository keywordRepository;
    @Mock
    ChoiceRepository choiceRepository;
    @Mock
    TopicRepository topicRepository;
    @Mock
    VoteRepository voteRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void before() {
        setEventPublisher(topicService, eventPublisher);
    }

    @Test
    void TopicCreateRequest_equalOrLessThanMaxLength25_success() {
        assertDoesNotThrow(() -> TopicTestDtoHelper.builder()
                .title("엄청길어요엄청길어요엄청길어요엄청길어요엄청길어요")
                .build().createRequest()
        );
    }

    @Test
    void TopicCreateRequest_greaterThanMaxLength25_throwsError() {
        assertThatThrownBy(() -> TopicTestDtoHelper.builder()
                .title("엄청길어요엄청길어요엄청길어요엄청길어요엄청길어요엄청길어요")
                .build().createRequest()
        ).isInstanceOf(LengthInvalidException.class);
    }

    @Test
    @DisplayName("토픽 생성 테스트")
    void createMembersTopic() {
        // given
        Member member = TestMember.builder()
                .id(1L)
                .build()
                .buildMember();

        Keyword keyword = TestKeyword.builder()
                .id(1L)
                .build()
                .buildKeyword();

        when(memberRepository.findByIdAndActiveTrue(anyLong())).thenReturn(Optional.of(member));
        when(keywordRepository.findByNameAndSide(any(), any())).thenReturn(Optional.of(keyword));

        // when
        TopicResponse topicResponse = topicService.createMembersTopic(
                member.getId(),
                TopicTestDtoHelper.builder()
                        .keywords(List.of(keyword))
                        .build().createRequest());

        // then
        assertThat(topicResponse.keywords().get(0).keywordId()).isEqualTo(keyword.getId());
    }

    @Test
    @DisplayName("토픽이 생성되면 이벤트 발행")
    void event_publish_when_topic_saved() {
        // given
        Member member = TestMember.builder()
                .id(1L)
                .build()
                .buildMember();

        when(memberRepository.findByIdAndActiveTrue(anyLong())).thenReturn(Optional.of(member));
        when(keywordRepository.findByNameAndSide(any(), any())).thenReturn(Optional.of(new Keyword("key", TopicSide.TOPIC_A)));

        TopicCreateRequest request = TopicTestDtoHelper.builder()
                .build()
                .createRequest();

        // when
        topicService.createMembersTopic(member.getId(), request);

        // then
        verify(eventPublisher).publishEvent(any(TopicCreateEvent.class));
    }

    @Test
    @DisplayName("토픽이 생성 중 예외 발생하면 이벤트 발행X")
    void topic_save_exception_test() {
        // given
        Member member = TestMember.builder()
                .id(1L)
                .build()
                .buildMember();

        when(memberRepository.findByIdAndActiveTrue(anyLong())).thenReturn(Optional.of(member));

        TopicCreateRequest request = TopicTestDtoHelper.builder()
                .build()
                .createRequest();

        try {
            // when
            topicService.createMembersTopic(member.getId(), request);
        } catch (RuntimeException e) {
            // then
            verify(eventPublisher, never()).publishEvent(any(TopicCreateEvent.class));
        }
    }

    private void setEventPublisher(TopicService topicService, ApplicationEventPublisher eventPublisher) {
        ReflectionTestUtils.setField(topicService, "eventPublisher", eventPublisher);
    }

    @Test
    @DisplayName("투표를 하면 voteAlready = true")
    void create_vote() {
        // given
        Long topicId = 1L;
        LocalDateTime deadline = LocalDateTime.now().plusHours(1);

        Member author = TestMember.builder()
                .id(1L)
                .build().buildMember();
        Member voter = TestMember.builder()
                .id(2L)
                .build().buildMember();

        Topic topic = TestTopic.builder()
                .id(topicId)
                .deadline(deadline)
                .author(author)
                .build().buildTopic();

        VoteRequest request = new VoteRequest(ChoiceOption.CHOICE_A, LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());

        when(memberRepository.findByIdAndActiveTrue(anyLong())).thenReturn(Optional.of(voter));
        when(topicRepository.findByIdAndActiveTrue(anyLong())).thenReturn(Optional.of(topic));

        // when
        topicService.voteForTopicByMember(topicId, 2L, request);

        // then
        assertThat(voter.votedAlready(topic)).isTrue();
    }

    @Test
    @DisplayName("투표 취소")
    void cancel_vote() {
        // given
        Long topicId = 1L;
        LocalDateTime deadline = LocalDateTime.now().plusHours(1);

        Member author = TestMember.builder()
                .id(1L)
                .build().buildMember();
        Member voter = TestMember.builder()
                .id(2L)
                .build().buildMember();

        Topic topic = TestTopic.builder()
                .id(topicId)
                .deadline(deadline)
                .author(author)
                .build().buildTopic();

        // Vote 생성
        Vote vote = new Vote(ChoiceOption.CHOICE_A, LocalDateTime.now());
        vote.associate(voter, topic);

        when(memberRepository.findByIdAndActiveTrue(anyLong())).thenReturn(Optional.of(voter));
        when(topicRepository.findByIdAndActiveTrue(anyLong())).thenReturn(Optional.of(topic));
        when(voteRepository.findByVoterIdAndTopicId(any(), any())).thenReturn(Optional.of(vote));

        // when
        topicService.cancelVoteForTopicByMember(topicId, 2L, new VoteCancelRequest(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()));

        // then
        assertThat(author.votedAlready(topic)).isFalse();
    }

    @Builder
    public static class TopicTestDtoHelper {

        @Builder.Default
        private List<Keyword> keywords = List.of(new Keyword("key1", TopicSide.TOPIC_A), new Keyword("key2", TopicSide.TOPIC_A));

        @Builder.Default
        private TopicSide topicSide = TopicSide.TOPIC_A;

        @Builder.Default
        private String title = "title";

        @Builder.Default
        private List<ChoiceCreateRequest> choices = List.of(
                new ChoiceCreateRequest(
                        ChoiceOption.CHOICE_A,
                        new ImageTextChoiceContentCreateRequest("imageUrl", "choiceA")),
                new ChoiceCreateRequest(
                        ChoiceOption.CHOICE_B,
                        new ImageTextChoiceContentCreateRequest(null, "choiceB"))
        );

        @Builder.Default
        private Long deadline = LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toEpochSecond();

        public TopicCreateRequest createRequest() {
            return TopicCreateRequest.builder()
                    .side(topicSide)
                    .keywordNames(keywords.stream().map(Keyword::getName).toList())
                    .title(title)
                    .choices(choices)
                    .deadline(deadline)
                    .build();
        }

        public TopicResponse createResponse() {
            List<ChoiceResponse> choiceResponses = new ArrayList<>();
            for (int i = 0; i < choices.size(); i++) {
                ChoiceCreateRequest choice = choices.get(i);
                choiceResponses.add(new ChoiceResponse(
                        (long) i,
                        new ImageTextChoiceContentResponse(
                                ((ImageTextChoiceContentCreateRequest) choice.choiceContentRequest()).text(),
                                ((ImageTextChoiceContentCreateRequest) choice.choiceContentRequest()).imageUrl()),
                        choice.choiceOption()));
            }

            List<KeywordResponse> keywordResponses = new ArrayList<>();
            for (int i = 0; i < keywords.size(); i++) {
                final Keyword keyword = keywords.get(i);
                keywordResponses.add(new KeywordResponse((long) i, keyword.getName(), keyword.getSide()));
            }
            return TopicResponse.builder()
                    .topicId(0L)
                    .topicSide(topicSide)
                    .topicTitle(title)
                    .keywords(keywordResponses)
                    .choices(choiceResponses)
                    .build();
        }
    }
}