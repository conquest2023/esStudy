package es.board.repository.entity.repository.infrastructure;

import es.board.repository.entity.CommentEntity;
import es.board.repository.entity.repository.infrastructure.projection.MyCommentProjection;
import es.board.repository.entity.repository.infrastructure.projection.PostsAndCommentsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentJpaRepository extends JpaRepository<CommentEntity,Long> {

    Page<CommentEntity> findAll(Pageable pageable);


    @Query(" SELECT c.postId AS postId, u.username AS username, c.content AS content, c.likeCount AS likeCount, c.createdAt AS createdAt " +
            " FROM CommentEntity c " +
            " INNER JOIN User u ON c.userId = u.userId" +
            " WHERE u.userId = :userId")
    Page<MyCommentProjection> findByMyPageUserComments(Pageable pageable, @Param("userId") String userId);


    @Query(" SELECT p.id AS id, " +
            "u.username AS username," +
            "p.title AS title," +
            "p.description AS description," +
            "p.viewCount AS viewCount," +
            "p.likeCount AS likeCount, " +
            "p.createdAt AS createdAt " +
            " FROM CommentEntity c  INNER  join PostEntity p on c.postId= p.id JOIN User u ON c.userId = u.userId" +
            " WHERE u.userId = :userId")
    Page<PostsAndCommentsProjection> findByMyPageUserPostsAndComments(Pageable pageable, @Param("userId") String userId);
    @Query("select c from CommentEntity c where c.postId=:id")
    List<CommentEntity> findById(int id);



}
