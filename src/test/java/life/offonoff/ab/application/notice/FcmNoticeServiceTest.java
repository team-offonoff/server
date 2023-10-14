package life.offonoff.ab.application.notice;

import life.offonoff.ab.application.event.topic.NoticedEvent;
import life.offonoff.ab.application.event.topic.VotingResult;
import life.offonoff.ab.domain.member.Member;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FcmNoticeServiceTest {

    @InjectMocks
    FcmNoticeService fcmNoticeService;
    @Mock
    MemberRepository memberRepository;
    @Mock
    ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void beforeEach() {
        setEventPublisher(fcmNoticeService, eventPublisher);
    }

    @Test
    @DisplayName("votingResult 공지 후 NoticedEvent 발행")
    void event_publish_test() {
        // given
        VotingResult result = createVotingResult(1L);

        when(memberRepository.findAllVotedTo(anyLong())).thenReturn(new ArrayList<Member>());

        // when
        fcmNoticeService.noticeVotingResult(result);

        // then
        verify(eventPublisher).publishEvent(any(NoticedEvent.class));
    }

    private void setEventPublisher(FcmNoticeService fcmNoticeService, ApplicationEventPublisher eventPublisher) {
        ReflectionTestUtils.setField(fcmNoticeService, "eventPublisher", eventPublisher);
    }

    private VotingResult createVotingResult(Long topicId) {
        return new VotingResult(
                topicId,
                "title",
                "category",
                "memberName",
                0
        );
    }
}