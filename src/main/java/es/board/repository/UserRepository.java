package es.board.repository;
import es.board.repository.entity.EsUser;
import  org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends  JpaRepository<EsUser,Integer> {

    @Query("select u.userId from EsUser u where u.userId = :id")
    String findByUserid(@Param("id") String id);


    Optional<EsUser> findByUserId(String username);


    @Query("SELECT COUNT(u) > 0 FROM EsUser u WHERE u.userId = :id AND u.password = :password")
    Boolean existsByUserIdAndPassword(@Param("id") String id, @Param("password") String password);
}
