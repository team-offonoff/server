package life.offonoff.ab.repository;

import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    /**
     * Paging, 모든 Topic조회 + Not Hidden by a Member
     */
    @Query("select t from Topic t " +
            "join fetch t.category c " +
            "where t.status = :status " +
            "and not exists(select 1 from HiddenTopic ht " +
                            "where ht.member.id = :memberId " +
                            " and ht.topic.id = t.id)")
    Slice<Topic> findAllNotHidden(
            @Param("status") TopicStatus status,
            @Param("memberId") Long memberId,
            Pageable pageable
    );

    /**
     * Paging, 모든 Topic조회 (category별) + Not Hidden by a Member
     */
    @Query("select t from Topic t " +
            "join fetch t.category c " +
            "where t.status = :status " +
            "and c.id = :categoryId " +
            "and not exists(select 1 from HiddenTopic ht " +
                            "where ht.member.id = :memberId " +
                            " and ht.topic.id = t.id)")
    Slice<Topic> findAllNotHiddenByCategoryId(
            @Param("status") TopicStatus status,
            @Param("memberId") Long memberId,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );
}
