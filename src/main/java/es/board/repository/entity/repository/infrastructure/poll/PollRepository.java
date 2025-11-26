package es.board.repository.entity.repository.infrastructure.poll;

import es.board.repository.entity.feed.PostEntity;
import es.board.repository.entity.poll.PollEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PollRepository {


    void save(PollEntity poll);

    List<Integer> findPollIds(int offset,int size);
    Page<PostEntity> findPollPagingList(Pageable pageable);

    Optional<PollEntity> findById(long poll);
    Optional<PollEntity> findByPostId(int postId);
    PollEntity findPollDetail(int id);
}
