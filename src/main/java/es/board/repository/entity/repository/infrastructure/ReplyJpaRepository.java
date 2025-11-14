package es.board.repository.entity.repository.infrastructure;

import es.board.repository.entity.ReplyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyJpaRepository extends JpaRepository<ReplyEntity,Long> {

    Page<ReplyEntity> findAll(Pageable pageable);


    @Query("select r from ReplyEntity r where r.postId=:id")
    List<ReplyEntity> findReplys(long id);
}
