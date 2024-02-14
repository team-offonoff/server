package life.offonoff.ab.application.notice;

import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.vote.VoteResult;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.repository.notice.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FcmNoticeServiceTest {

    @InjectMocks
    DefaultNoticeService noticeService;
    @Mock
    MemberRepository memberRepository;
    @Mock
    NotificationRepository notificationRepository;

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

        VoteResult result = new VoteResult();
        result.setTopic(topic);

        List<Member> voteMembers = List.of(member);

        when(memberRepository.findAllVotedTo(anyLong())).thenReturn(voteMembers);

        // when
        noticeService.noticeVoteResult(result);

        // then
        assertThat(member.getNotifications().size()).isGreaterThan(0);
    }
}