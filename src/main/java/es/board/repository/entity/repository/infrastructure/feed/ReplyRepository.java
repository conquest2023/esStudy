package es.board.repository.entity.repository.infrastructure.feed;

import es.board.repository.entity.CommentEntity;
import es.board.repository.entity.ReplyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReplyRepository {


    void saveReply(ReplyEntity reply);


//    String findByUserId(String )
    List<ReplyEntity> findByReplys(int id);

    Optional<ReplyEntity> isExist(long id);
    Page<ReplyEntity> findByPageReplys(Pageable pageable);

    ReplyEntity findReplyDetail(long id);

    void deleteReply(long id);
}
