package es.board.repository.entity.repository.infrastructure.poll;

import es.board.controller.model.dto.poll.PollVoteDTO;
import es.board.repository.entity.poll.PollVoteEntity;
import es.board.repository.entity.repository.infrastructure.projection.VoteOptionProjection;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface PollVoteRepository {

    List<VoteOptionProjection> getVoteDetail(long pollId);


    void vote(PollVoteEntity vote);


    Set<String> findVoters(long pollId);

}
