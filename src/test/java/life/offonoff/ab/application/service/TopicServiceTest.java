package life.offonoff.ab.application.service;

import life.offonoff.ab.application.event.topic.TopicCreateEvent;
import life.offonoff.ab.application.service.request.ChoiceCreateRequest;
import life.offonoff.ab.application.service.request.ImageTextChoiceContentCreateRequest;
import life.offonoff.ab.application.service.request.TopicCreateRequest;
import life.offonoff.ab.application.service.request.VoteRequest;
import life.offonoff.ab.domain.category.Category;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.domain.vote.Vote;
import life.offonoff.ab.exception.LengthInvalidException;
import life.offonoff.ab.repository.CategoryRepository;
import life.offonoff.ab.repository.ChoiceRepository;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.repository.topic.TopicRepository;
import life.offonoff.ab.web.response.ChoiceResponse;
import life.offonoff.ab.web.response.ImageTextChoiceContentResponse;
import life.offonoff.ab.web.response.TopicResponse;
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
import static life.offonoff.ab.domain.TestEntityUtil.TestCategory;
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
    CategoryRepository categoryRepository;
    @Mock
    ChoiceRepository choiceRepository;
    @Mock
    TopicRepository topicRepository;
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
                .topicTitle("엄청길어요엄청길어요엄청길어요엄청길어요엄청길어요")
                .build().createRequest()
        );
    }

    @Test
    void TopicCreateRequest_greaterThanMaxLength25_throwsError() {
        assertThatThrownBy(() -> TopicTestDtoHelper.builder()
                .topicTitle("엄청길어요엄청길어요엄청길어요엄청길어요엄청길어요엄청길어요")
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

        Category category = TestCategory.builder()
                .id(1L)
                .build()
                .buildCategory();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

        // when
        TopicResponse topicResponse = topicService.createMembersTopic(
                member.getId(),
                TopicTestDtoHelper.builder()
                        .category(category)
                        .build().createRequest());

        // then
        assertThat(topicResponse.categoryId()).isEqualTo(category.getId());
    }

    @Test
    @DisplayName("토픽이 생성되면 이벤트 발행")
    void event_publish_when_topic_saved() {
        // given
        Member member = TestMember.builder()
                .id(1L)
                .build()
                .buildMember();

        Category category = TestCategory.builder()
                .id(1L)
                .build()
                .buildCategory();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

        TopicCreateRequest request = TopicTestDtoHelper.builder()
                .category(category)
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

        Category category = TestCategory.builder()
                .id(1L)
                .build()
                .buildCategory();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(topicRepository.save(any())).thenThrow(RuntimeException.class);

        TopicCreateRequest request = TopicTestDtoHelper.builder()
                .category(category)
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
        Long memberId = 1L;
        LocalDateTime deadline = LocalDateTime.now();

        Member member = TestMember.builder()
                .id(memberId)
                .build().buildMember();

        Topic topic = TestTopic.builder()
                .id(topicId)
                .deadline(deadline)
                .build().buildTopic();

        VoteRequest request = new VoteRequest(member.getId(), ChoiceOption.CHOICE_A, deadline.minusDays(1), true);

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(topicRepository.findById(anyLong())).thenReturn(Optional.of(topic));

        // when
        topicService.vote(topicId, request);

        // then
        assertThat(member.votedAlready(topic)).isTrue();
    }

    @Test
    @DisplayName("투표 취소")
    void cancel_vote() {
        // given
        Long topicId = 1L;
        Long memberId = 1L;
        LocalDateTime deadline = LocalDateTime.now();

        Member member = TestMember.builder()
                .id(memberId)
                .build().buildMember();

        Topic topic = TestTopic.builder()
                .id(topicId)
                .deadline(deadline)
                .build().buildTopic();

        // Vote 생성
        Vote vote = new Vote(ChoiceOption.CHOICE_A);
        vote.associate(member, topic);

        VoteRequest request = new VoteRequest(member.getId(), ChoiceOption.CHOICE_A, deadline.minusDays(1), false);

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(topicRepository.findById(anyLong())).thenReturn(Optional.of(topic));

        // when
        topicService.vote(topicId, request);

        // then
        assertThat(member.votedAlready(topic)).isFalse();
    }

    @Builder
    public static class TopicTestDtoHelper {

        private Category category;

        @Builder.Default
        private TopicSide topicSide = TopicSide.TOPIC_A;

        @Builder.Default
        private String topicTitle = "title";

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
        private Long deadline = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();

        public TopicCreateRequest createRequest() {
            Long categoryId = 0L;
            if (category != null) {
                categoryId = category.getId();
            }
            return TopicCreateRequest.builder()
                    .topicSide(topicSide)
                    .categoryId(categoryId)
                    .topicTitle(topicTitle)
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

            Long categoryId = 0L;
            if (category != null) {
                categoryId = category.getId();
            }
            return TopicResponse.builder()
                    .topicId(0L)
                    .topicSide(topicSide)
                    .topicTitle(topicTitle)
                    .categoryId(categoryId)
                    .choices(choiceResponses)
                    .build();
        }
    }
}