package es.board.repository.entity.entityrepository;
import es.board.repository.entity.EsUser;
import jakarta.transaction.Transactional;
import  org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository  extends  JpaRepository<EsUser,Integer> {

    @Query("select u.userId from EsUser u where u.userId = :id")
    String findByUserid(@Param("id") String id);

    @Modifying
    @Transactional
    @Query("UPDATE EsUser u SET u.lastLogin = :lastLogin WHERE u.userId = :userId")
    void updateLastLogin(@Param("userId") String userId,
                         @Param("lastLogin") LocalDateTime lastLogin);


    Optional<EsUser> findByUserId(String username);

    @Query("SELECT u.username from  EsUser u  WHERE u.username = :username")
    String findByUserIdOne(String username);


    @Query("SELECT COUNT(u) > 0 FROM EsUser u WHERE u.userId = :id AND u.password = :password")
    Boolean existsByUserIdAndPassword(@Param("id") String id, @Param("password") String password);
}
