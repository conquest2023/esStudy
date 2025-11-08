package es.board.repository.entity.repository.infrastructure.feed;

import es.board.repository.entity.CommentEntity;
import es.board.repository.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository {


    void saveComment(CommentEntity post);


    List<CommentEntity> findByComments(int id);

    Page<CommentEntity> findByPageComments(Pageable pageable);

    CommentEntity findCommentDetail(long id);

    void deleteComment(long id);

}
