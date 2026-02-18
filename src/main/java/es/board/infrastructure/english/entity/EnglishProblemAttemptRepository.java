package es.board.infrastructure.english.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EnglishProblemAttemptRepository
        extends JpaRepository<EnglishProblemAttempt, Long> {

    Page<EnglishProblemAttempt> findByUserIdOrderByCreatedAtDesc(
            String userId,
            Pageable pageable
    );

    @Query("select e from EnglishProblemAttempt e " +
            " where e.userId in :userId ")
    List<EnglishProblemAttempt> findByUserLog(List<String> userId);

    Page<EnglishProblemAttempt> findByUserIdAndObjectId(
            String userId,
            String objectId,
            Pageable pageable
    );
}
