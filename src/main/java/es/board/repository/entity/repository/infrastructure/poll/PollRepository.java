package es.board.repository.entity.repository.infrastructure.poll;

import es.board.repository.entity.poll.PollEntity;

import java.util.Optional;

public interface PollRepository {


    void save(PollEntity poll);


    Optional<PollEntity> findById(long poll);
    Optional<PollEntity> findByPostId(int postId);
    PollEntity findPollDetail(int id);
}
