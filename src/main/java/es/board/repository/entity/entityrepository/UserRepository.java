package es.board.repository.entity.entityrepository;
import es.board.repository.entity.User;
import jakarta.transaction.Transactional;
import  org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository  extends  JpaRepository<User,Integer> {


    @Query("select u.username from User u where u.userId = :id")
    String findByUsername(@Param("id") String id);


    @Query("select u.userId from User u where u.userId = :id")
    String findByUserid(@Param("id") String id);


    @Query("SELECT u.userId FROM User u")
    List<String> findAllUserIds();

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastLogin = :lastLogin WHERE u.userId = :userId")
    void updateLastLogin(@Param("userId") String userId,
                         @Param("lastLogin") LocalDateTime lastLogin);


    Optional<User> findByUserId(String username);

    @Query("SELECT u.username from  User u  WHERE u.username = :username")
    String findByUserIdOne(String username);


    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.userId = :id AND u.password = :password")
    Boolean existsByUserIdAndPassword(@Param("id") String id, @Param("password") String password);
}
