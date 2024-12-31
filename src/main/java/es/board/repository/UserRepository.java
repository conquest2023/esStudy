package es.board.repository;
import es.board.repository.document.EsUser;
import  org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository  extends  JpaRepository<EsUser,Integer> {

    @Query("select u.userId from EsUser u where u.userId = :id")
    String findByUserId(@Param("id") String id);
}
