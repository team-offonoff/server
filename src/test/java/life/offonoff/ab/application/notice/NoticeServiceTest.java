package life.offonoff.ab.application.notice;

import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.notice.DefaultNotification;
import life.offonoff.ab.domain.notice.Notification;
import life.offonoff.ab.domain.notice.VoteCountOnTopicNotification;
import life.offonoff.ab.domain.notice.VoteResultNotification;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.vote.VoteResult;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.repository.notice.NotificationRepository;
import life.offonoff.ab.web.response.notice.DefaultNoticeResponse;
import life.offonoff.ab.web.response.notice.NoticeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static life.offonoff.ab.application.notice.NoticeService.VOTE_COUNT_MODULO;
import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @InjectMocks
    NoticeService noticeService;
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

        VoteResult result = new VoteResult();
        result.setTopic(topic);

        List<Member> voteMembers = List.of(voter);

        when(memberRepository.findAllListeningVoteResultAndVotedTopicId(anyLong())).thenReturn(voteMembers);

        // when
        noticeService.noticeVoteResult(result);

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

        VoteResult result = new VoteResult();
        result.setTopic(topic);

        when(memberRepository.findAllListeningVoteResultAndVotedTopicId(anyLong())).thenReturn(Collections.emptyList());

        // when
        noticeService.noticeVoteResult(result);

        // then
        assertThat(author.getNotifications().size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Vote Count 알림 기준에 해당하면 알림을 생성한다.")
    void topic_voteCount_should_noticed() {
        // given
        Topic topic = TestTopic.builder()
                .id(1L)
                .voteCount(VOTE_COUNT_MODULO)
                .build().buildTopic();

        // when
        boolean shouldNotice = noticeService.shouldNoticeVoteCountForTopic(topic);

        // then
        assertThat(shouldNotice).isTrue();
    }

    @Test
    void find_NoticeResponses() {
        // given
        Member receiver = TestMember.builder()
                .id(1L)
                .build().buildMember();
        DefaultNotification notification = new DefaultNotification(receiver, "title", "content");

        when(notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(anyLong()))
                .thenReturn(List.of(notification));

        // when
        List<NoticeResponse> responses = noticeService.findAllByReceiverId(receiver.getId());

        // then
        assertThat(responses.size()).isGreaterThan(0);
    }
}