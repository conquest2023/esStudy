package es.board.repository.entity.repository.infrastructure.feed;

import es.board.repository.entity.feed.CommentEntity;
import es.board.repository.entity.repository.infrastructure.projection.MyCommentProjection;
import es.board.repository.entity.repository.infrastructure.projection.PostsAndCommentsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository {


    void saveComment(CommentEntity post);

    String findByUserId(long commentId);
    List<CommentEntity> findByComments(int id);

    Optional<CommentEntity> isExist(long id);
    Page<MyCommentProjection> findUserMyPageComments(Pageable pageable, String userId);

    Page<PostsAndCommentsProjection> findMyPagePostsAndComments(Pageable pageable,String userId);

    CommentEntity findCommentDetail(long id);

    void deleteComment(long id);

}
