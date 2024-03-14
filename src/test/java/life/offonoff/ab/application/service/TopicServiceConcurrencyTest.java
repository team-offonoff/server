package life.offonoff.ab.application.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import life.offonoff.ab.application.service.member.MemberService;
import life.offonoff.ab.application.service.request.VoteRequest;
import life.offonoff.ab.application.testutil.AbCleaner;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.choice.Choice;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.exception.VoteConcurrencyException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.TestTransaction;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static life.offonoff.ab.domain.TestEntityUtil.createRandomMember;
import static life.offonoff.ab.domain.TestEntityUtil.createRandomTopicByMemberWithChoices;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class TopicServiceConcurrencyTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private EntityManager em;

    @Autowired
    private AbCleaner cleaner;

    @AfterEach
    void tearDown() {
        cleaner.cleanTables();
    }

    @Test
    void voteForTopicByMember() throws InterruptedException {
        // given
        final int COUNT = 10;
        final ExecutorService executorService = Executors.newFixedThreadPool(COUNT);

        // 토픽 생성
        Member topicAuthor = createRandomMember();
        em.persist(topicAuthor);
        Topic topic = createRandomTopicByMemberWithChoices(
                topicAuthor, TopicSide.TOPIC_A, ChoiceOption.CHOICE_A, ChoiceOption.CHOICE_B);
        em.persist(topic);

        // 투표자 COUNT만큼 생성
        List<Member> voters = IntStream.range(0, COUNT)
                .mapToObj(__ -> {
                    Member voter = createRandomMember();
                    em.persist(voter);
                    return voter;
                })
                .toList();

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        // when
        long votedAt = System.currentTimeMillis() / 1000;
        final CountDownLatch latch = new CountDownLatch(COUNT);
        AtomicInteger failureCounter = new AtomicInteger(0);
        voters.forEach(voter -> {
            executorService.execute(() -> {
                try {
                    topicService.voteForTopicByMember(
                            topic.getId(), voter.getId(), new VoteRequest(ChoiceOption.CHOICE_A, votedAt));
                } catch (VoteConcurrencyException e) {
                    failureCounter.incrementAndGet();
                }
                latch.countDown();
            });
        });
        latch.await();

        // then
        Topic updatedTopic = em.find(Topic.class, topic.getId());
        Choice votedChoice = updatedTopic.getChoices().stream().filter(c -> c.getChoiceOption().equals(ChoiceOption.CHOICE_A)).findAny().get();
        // 투표수는 COUNT와 동일해야함
        int successfulVoteCount = COUNT - failureCounter.get();
        assertThat(updatedTopic.getVoteCount()).isEqualTo(successfulVoteCount);
        assertThat(votedChoice.getVoteCount()).isEqualTo(successfulVoteCount);
    }

}
