package es.board.repository.entity.repository.infrastructure.jpa;

import es.board.repository.entity.poll.PollOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PollOptionJpaRepository extends JpaRepository<PollOptionEntity,Integer> {



    @Query("select op from PollOptionEntity op where op.poll.id=:optionId and op.id=:pollId")
    Optional<PollOptionEntity> isCheckOption(@Param("pollId") long pollId,
                                             @Param("optionId") long  optionId);
}
