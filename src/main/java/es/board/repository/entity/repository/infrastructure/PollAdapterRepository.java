package es.board.repository.entity.repository.infrastructure;

import es.board.repository.entity.poll.PollEntity;
import es.board.repository.entity.repository.infrastructure.jpa.PollJpaRepository;
import es.board.repository.entity.repository.infrastructure.poll.PollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class PollAdapterRepository implements PollRepository {

    private final PollJpaRepository pollJpaRepository;

    @Override
    public void save(PollEntity poll) {

        pollJpaRepository.save(poll);
    }

    @Override
    public Optional<PollEntity> findById(long poll) {
        return pollJpaRepository.findByPollId(poll);
    }

    @Override
    public Optional<PollEntity> findByPostId(int postId) {
        return pollJpaRepository.findByPostId(postId);
    }

    @Override
    public PollEntity findPollDetail(int postId) {
        return pollJpaRepository.findPollDetail(postId);
    }
}
