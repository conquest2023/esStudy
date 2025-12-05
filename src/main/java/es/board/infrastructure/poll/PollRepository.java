package es.board.infrastructure.poll;

import es.board.infrastructure.entity.feed.PostEntity;
import es.board.infrastructure.entity.poll.PollEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PollRepository {


    void save(PollEntity poll);

    List<PollEntity> findBy3DaysAgoPoll(LocalDateTime day);
    List<Integer> findPollIds(int offset,int size);
    Optional<PostEntity> findByPost(long pollId);
    Page<PostEntity> findPollPagingList(Pageable pageable);

    Optional<PollEntity> findById(long poll);
    Optional<PollEntity> findByPostId(int postId);
    PollEntity findPollDetail(int id);
}
