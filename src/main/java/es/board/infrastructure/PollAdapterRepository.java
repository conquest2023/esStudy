package es.board.infrastructure;

import es.board.domain.Post;
import es.board.infrastructure.poll.PollRepository;
import es.board.infrastructure.entity.feed.PostEntity;
import es.board.infrastructure.entity.poll.PollEntity;
import es.board.infrastructure.jpa.PollJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
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
    public List<PollEntity> findBy3DaysAgoPoll(LocalDateTime day) {
        LocalDateTime dateTime = day.minusDays(3);
        return pollJpaRepository.findBy3DaysAgoPoll(dateTime);
    }

    @Override
    public List<Integer> findPollIds(int offset, int size) {
        return pollJpaRepository.findPollIds(offset,size);
    }

    @Override
    public Optional<PostEntity> findByPost(long id) {
        return pollJpaRepository.findByPost(id);
    }

    @Override
    public Page<PostEntity> findPollPagingList(Pageable pageable) {
        return pollJpaRepository.findByPollPagingList(pageable);
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
