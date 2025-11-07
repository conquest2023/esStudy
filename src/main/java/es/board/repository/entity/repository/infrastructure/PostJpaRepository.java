package es.board.repository.entity.repository.infrastructure;

import es.board.repository.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostJpaRepository  extends JpaRepository<PostEntity,Integer> {

    Page<PostEntity> findAll(Pageable pageable);
}
