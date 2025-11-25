package es.board.repository.entity.repository.infrastructure.feed;

import es.board.repository.entity.feed.ReplyEntity;
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

//    String findByUserId(String )
    List<ReplyEntity> findByReplys(int id);

    Optional<ReplyEntity> isExist(long id);

    Page<ReplyEntity> findByPageReplys(Pageable pageable);


    void deleteReply(long id);
}
