package es.board.domain;

import es.board.infrastructure.entity.feed.ReplyEntity;
import es.board.infrastructure.feed.ReplyAggView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReplyRepository {


    void saveReply(ReplyEntity reply);
    List<ReplyAggView> countRepliesIn(@Param("postIds") List<Integer> postIds);


    List<ReplyEntity> findByReplys(int id);

    Optional<ReplyEntity> isExist(long id);

    Page<ReplyEntity> findByPageReplys(Pageable pageable);


    void deleteReply(long id);
}
