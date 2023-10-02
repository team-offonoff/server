package life.offonoff.ab.service;

import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.domain.topic.hide.HiddenTopic;
import life.offonoff.ab.repository.TopicRepository;
import life.offonoff.ab.service.dto.TopicSearchParams;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final MemberService memberService;

    public Topic searchById(Long topicId) {
        return topicRepository.findById(topicId)
                              .orElseThrow();
    }

    public Slice<Topic> searchAllNotHidden(TopicSearchParams params, Pageable pageable) {
        TopicStatus topicStatus = params.getTopicStatus();
        Long memberId = params.getMemberId();
        Long categoryId = params.getCategoryId();

        if (categoryId == null) {
            return topicRepository.findAllNotHidden(topicStatus, memberId, pageable);
        }
        return topicRepository.findAllNotHiddenByCategoryId(topicStatus, memberId, categoryId, pageable);
    }

    @Transactional
    public void hide(Long memberId, Long topicId, Boolean hide) {
        Member member = memberService.searchById(memberId);
        Topic topic = this.searchById(topicId);

        if (hide) {
            doHide(member, topic);
            return;
        }
        cancelHide(member, topic);
    }

    private void doHide(Member member, Topic topic) {
        if (!member.hideAlready(topic)) {
            HiddenTopic hiddenTopic = new HiddenTopic();
            hiddenTopic.associate(member, topic);
        }
    }

    private void cancelHide(Member member, Topic topic) {
        if (member.hideAlready(topic)) {
            topic.removeHiddenBy(member);
        }
    }
}
