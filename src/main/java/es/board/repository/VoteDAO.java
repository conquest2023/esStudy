package es.board.repository;

import es.board.controller.model.req.VoteResponse;
import es.board.repository.entity.Vote;

public interface VoteDAO {

    void saveAgreeVote(VoteResponse voteResponse, Long id);
}
