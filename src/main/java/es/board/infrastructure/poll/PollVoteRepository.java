package es.board.infrastructure.poll;

import es.board.infrastructure.projection.VoteOptionProjection;
import es.board.infrastructure.entity.poll.PollVoteEntity;

import java.util.List;
import java.util.Set;

public interface PollVoteRepository {

    List<VoteOptionProjection> getVoteDetail(long pollId);


    void vote(PollVoteEntity vote);

    void voteAll(List<PollVoteEntity> votes);

    Set<String> findVoters(long pollId);

}
