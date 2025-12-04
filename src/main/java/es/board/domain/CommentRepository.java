package es.board.domain;

import es.board.infrastructure.entity.feed.CommentEntity;
import es.board.infrastructure.feed.CommentAggView;
import es.board.infrastructure.projection.MyCommentProjection;
import es.board.infrastructure.projection.PostsAndCommentsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository {

    List<CommentAggView> countCommentsIn(List<Integer> postIds);
    CommentEntity saveComment(CommentEntity post);

    Optional<CommentEntity> findById(long id);
    String findByUserId(long commentId);
    List<CommentEntity> findByComments(int id);

    Optional<CommentEntity> isExist(long id);
    Page<MyCommentProjection> findUserMyPageComments(int page,int size, String userId);

    Page<PostsAndCommentsProjection> findMyPagePostsAndComments(int page,int size,String userId);

    void deleteComment(long id);

}
