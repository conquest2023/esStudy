package es.board.infrastructure;

import es.board.infrastructure.projection.VoteOptionProjection;
import es.board.infrastructure.entity.poll.PollVoteEntity;
import es.board.infrastructure.jpa.PollVoteJpaRepository;
import es.board.infrastructure.poll.PollVoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class PollVoteAdapterRepository implements PollVoteRepository {


    private final PollVoteJpaRepository pollVoteRepository;

    @Override
    public List<VoteOptionProjection> getVoteDetail(long postId) {
        return null;
    }
    @Override
    public void vote(PollVoteEntity vote) {

        pollVoteRepository.save(vote);
    }

    @Override
    public void voteAll(List<PollVoteEntity> votes) {
        pollVoteRepository.saveAll(votes);
    }

    @Override
    public Set<String> findVoters(long pollId) {
        return pollVoteRepository.findVoters(pollId);
    }
}
