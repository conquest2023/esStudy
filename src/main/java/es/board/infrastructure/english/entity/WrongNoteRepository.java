package es.board.infrastructure.english.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface WrongNoteRepository
        extends JpaRepository<WrongNote, Long> {

    Optional<WrongNote> findByUserIdAndObjectId(String userId, String objectId);



    @Query("SELECT wn FROM WrongNote wn " +
            "WHERE wn.userId = :userId " +
            "AND wn.category = :category " +
            "AND wn.resolved = false")
    Page<WrongNote> findByUserIdAndResolvedFalseOrderByLastWrongAtDesc(
            @Param("userId") String userId,
            @Param("category") String category,
            Pageable pageable
    );

    @Transactional
    @Modifying
    @Query(value = """
        INSERT INTO wrong_note (
            user_id, object_id, category, part, level,
            wrong_count, last_wrong_at, first_wrong_at, resolved
        ) VALUES (
            :userId, :objectId, :category, :part, :level,
            1, NOW(), NOW(), false
        )
        ON DUPLICATE KEY UPDATE
            wrong_count = wrong_count + 1,
            last_wrong_at = NOW(),
            resolved = false,
            resolved_at = NULL
        """, nativeQuery = true)
    void upsertWrongNote(
            @Param("userId") String userId,
            @Param("objectId") String objectId,
            @Param("category") String category,
            @Param("part") Integer part,
            @Param("level") String level
    );

    @Transactional
    @Modifying
    @Query("""
        UPDATE WrongNote w
        SET w.resolved = true,
            w.resolvedAt = CURRENT_TIMESTAMP
        WHERE w.userId = :userId
          AND w.objectId = :objectId
        """)
    int resolve(
            @Param("userId") String userId,
            @Param("objectId") String objectId
    );
}

