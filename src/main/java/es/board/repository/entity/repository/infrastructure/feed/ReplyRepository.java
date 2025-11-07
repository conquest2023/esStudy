package es.board.repository.entity.repository.infrastructure.feed;

import es.board.repository.entity.CommentEntity;
import es.board.repository.entity.ReplyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository {


    void saveReply(ReplyEntity reply);


    List<ReplyEntity> findByReplys(int id);

    Page<ReplyEntity> findByPageReplys(Pageable pageable);

    ReplyEntity findReplyDetail(long id);
}
