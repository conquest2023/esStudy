package es.board.infrastructure.english.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnglishProblemAttemptRepository
        extends JpaRepository<EnglishProblemAttempt, Long> {

    Page<EnglishProblemAttempt> findByUserIdOrderByCreatedAtDesc(
            String userId,
            Pageable pageable
    );

    Page<EnglishProblemAttempt> findByUserIdAndObjectId(
            String userId,
            String objectId,
            Pageable pageable
    );
}
