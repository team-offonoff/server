package life.offonoff.ab.application.notice;

import life.offonoff.ab.application.event.topic.NoticedEvent;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.vote.VotingResult;
import life.offonoff.ab.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.ArrayList;
import java.util.List;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FcmNoticeServiceTest {

    @InjectMocks
    FcmNoticeService noticeService;
    @Mock
    MemberRepository memberRepository;
    @Mock
    ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void beforeEach() {
        setEventPublisher(noticeService, eventPublisher);
    }

    @Test
    @DisplayName("투표 결과 알림 후, Member에 VotingResultNotification 추가")
    void notice_then_create_VotingResultNotification() {
        // given
        Member member = TestMember.builder()
                .id(1L)
                .build().buildMember();

        Topic topic = TestTopic.builder()
                .id(1L)
                .voteCount(1000)
                .build().buildTopic();

        VotingResult result = new VotingResult();
        result.setTopic(topic);

        List<Member> voteMembers = List.of(member);

        when(memberRepository.findAllVotedTo(anyLong())).thenReturn(voteMembers);

        // when
        noticeService.noticeVotingResult(result);
        // then
        assertThat(member.getNotifications().size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("votingResult 공지 후 NoticedEvent 발행")
    void event_publish_test() {
        // given
        VotingResult result = new VotingResult();
        result.setTopic(TestTopic.builder()
                .id(1L)
                .build().buildTopic());

        when(memberRepository.findAllVotedTo(anyLong())).thenReturn(new ArrayList<Member>());

        // when
        noticeService.noticeVotingResult(result);

        // then
        verify(eventPublisher).publishEvent(any(NoticedEvent.class));
    }

    private void setEventPublisher(NoticeService noticeService, ApplicationEventPublisher eventPublisher) {
        ReflectionTestUtils.setField(noticeService, "eventPublisher", eventPublisher);
    }
}