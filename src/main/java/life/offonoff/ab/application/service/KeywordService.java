package life.offonoff.ab.application.service;

import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.repository.keyword.KeywordRepository;
import life.offonoff.ab.web.response.KeywordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class KeywordService {

    private final KeywordRepository keywordRepository;

    public Slice<KeywordResponse> findAllByTopicSide(TopicSide side, Pageable pageable) {

        return keywordRepository.findAllByTopicSide(side, pageable)
                                .map(KeywordResponse::from);
    }
}
