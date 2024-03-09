package life.offonoff.ab.application.notification;

import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.notification.DefaultNotification;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.repository.notfication.NotificationRepository;
import life.offonoff.ab.web.response.notification.NotificationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    NotificationService notificationService;
    @Mock
    MemberRepository memberRepository;
    @Mock
    NotificationRepository notificationRepository;

    @Test
    @DisplayName("투표 결과 알림 후, 투표자에게 VoteResultNotification 추가")
    void notice_VoteResult_to_voter() {
        // given
        Member voter = TestMember.builder()
                .id(1L)
                .build().buildMember();

        Topic topic = TestTopic.builder()
                .id(1L)
                .voteCount(1000)
                .build().buildTopic();

        List<Member> voteMembers = List.of(voter);

        when(memberRepository.findAllListeningVoteResultAndVotedTopicId(anyLong())).thenReturn(voteMembers);

        // when
        notificationService.notifyVoteResult(topic);

        // then
        assertThat(voter.getNotifications().size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("투표 종료 후, 토픽 작성자에게 VoteResultNotification 추가")
    void notice_VoteResult_to_author() {
        // given
        Member author = TestMember.builder()
                .id(1L)
                .build().buildMember();

        Topic topic = TestTopic.builder()
                .id(1L)
                .author(author)
                .voteCount(1000)
                .build().buildTopic();

        when(memberRepository.findAllListeningVoteResultAndVotedTopicId(anyLong())).thenReturn(Collections.emptyList());

        // when
        notificationService.notifyVoteResult(topic);

        // then
        assertThat(author.getNotifications().size()).isGreaterThan(0);
    }

    @Test
    void find_NoticeResponses() {
        // given
        Member receiver = TestMember.builder()
                .id(1L)
                .build().buildMember();
        DefaultNotification notification = new DefaultNotification(receiver, "title", "content");

        when(notificationRepository.findAllOrderByCreatedAtDesc(anyLong(), any()))
                .thenReturn(List.of(notification));

        // when
        List<NotificationResponse> responses = notificationService.findAllByReceiverId(receiver.getId());

        // then
        assertThat(responses.size()).isGreaterThan(0);
    }
}