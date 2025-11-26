package es.board.infrastructure.jpa;

import es.board.infrastructure.projection.VoteOptionProjection;
import es.board.infrastructure.entity.poll.PollVoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PollVoteJpaRepository  extends JpaRepository<PollVoteEntity,Integer> {

    @Query("select sum(vote.option.id) as sumValue from PollVoteEntity vote " +
            "where vote.poll.id =:pollId group by vote.option.id" )
    List<VoteOptionProjection> getVoteDetail(long pollId);

    @Query("""
    select distinct vote.voterId
        from PollVoteEntity vote
    where vote.poll.id = :pollId
    """)
    Set<String> findVoters(long pollId);

}
