package es.board.infrastructure.poll;

import es.board.infrastructure.entity.feed.PostEntity;
import es.board.infrastructure.entity.poll.PollEntity;
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
