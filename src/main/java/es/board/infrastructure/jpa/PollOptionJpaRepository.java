package es.board.infrastructure.jpa;

import es.board.infrastructure.entity.poll.PollOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PollOptionJpaRepository extends JpaRepository<PollOptionEntity,Integer> {



    @Query("select op from PollOptionEntity op where op.poll.id=:optionId and op.id=:pollId")
    Optional<PollOptionEntity> isCheckOption(@Param("pollId") long pollId,
                                             @Param("optionId") long  optionId);


    @Query("select op from PollOptionEntity op " +
            "where op.poll.id = :pollId and op.id in :optionIds")
    Optional<List<PollOptionEntity>> isCheckOptionList(@Param("pollId") long pollId,
                                                 @Param("optionIds") List<Long> optionIds);
}
