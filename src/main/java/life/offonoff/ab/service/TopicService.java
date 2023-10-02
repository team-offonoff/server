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
    public void hide(Long memberId, Long topicId) {
        Member member = memberService.searchById(memberId);
        Topic topic = this.searchById(topicId);

        // 이미 hide한 member면 예외
        if (topic.hidedBy(member)) {
            return; // 예외처리 필요
        }

        HiddenTopic hiddenTopic = new HiddenTopic();
        hiddenTopic.associate(member, topic);
    }
}
