package es.board.repository.entity.repository.infrastructure;

import es.board.repository.entity.poll.PollVoteEntity;
import es.board.repository.entity.repository.infrastructure.jpa.PollVoteJpaRepository;
import es.board.repository.entity.repository.infrastructure.poll.PollVoteRepository;
import es.board.repository.entity.repository.infrastructure.projection.VoteOptionProjection;
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
    public Set<String> findVoters(long pollId) {
        return pollVoteRepository.findVoters(pollId);
    }
}
