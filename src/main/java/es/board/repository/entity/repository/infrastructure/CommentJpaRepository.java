package es.board.repository.entity.repository.infrastructure;

import es.board.repository.entity.CommentEntity;
import es.board.repository.entity.PostEntity;
import es.board.repository.entity.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentJpaRepository extends JpaRepository<CommentEntity,Long> {

    Page<CommentEntity> findAll(Pageable pageable);


    @Query("select p from CommentEntity p where p.userId=:userId")
    Page<CommentEntity> findByMyPageUserComments(Pageable pageable,String userId);
    @Query("select c from CommentEntity c where c.postId=:id")
    List<CommentEntity> findById(int id);




//    @Query("")
}
