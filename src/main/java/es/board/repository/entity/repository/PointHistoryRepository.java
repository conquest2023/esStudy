package es.board.repository.entity.repository;

import es.board.repository.entity.PointHistoryEntity;
import es.board.infrastructure.jpa.projection.UserPointProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistoryEntity, Long> {

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE PointHistoryEntity u SET u.pointChange = u.pointChange + :amount WHERE u.userId = :userId")
    void incrementPoint(@Param("userId") String userId, @Param("amount") int amount);



    @Query("select sum(p.pointChange) from PointHistoryEntity p where p.userId=:userId")
    int sumPointUser(@Param("userId") String userId);

    @Query("SELECT sum(p.pointChange) as totalCount, " +
            "u.username as username " +
            "FROM PointHistoryEntity p " +
            " inner join User  u on p.userId = u.userId " +
            " WHERE u.username NOT IN ('asd', 'hoeng') " +
            "GROUP BY u.username " +
            "ORDER BY totalCount DESC " +
            "LIMIT 5")
    List<UserPointProjection> sumPointUserTop5();

    @Query( value = "SELECT p.user_id " +
            "FROM point_history p " +
            " GROUP BY p.user_id " +
            " ORDER BY SUM(p.point_change) DESC ",
            nativeQuery = true)
    List<String> findTop1PointUser();


    @Query("SELECT sum(p.pointChange) as totalCount, " +
            "u.username as username " +
            "FROM PointHistoryEntity p " +
            " inner join User  u on p.userId = u.userId " +
            " WHERE u.username NOT IN ('asd', 'hoeng' ,'호문무권신','잠수브로','머신는자','하이','공시준비') and p.createdAt >=:sevenDaysAgo" +
            " GROUP BY u.username " +
            "ORDER BY totalCount DESC " +
            "LIMIT 5")
    List<UserPointProjection> sumPointUserRecentTop5(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);



    @Query("SELECT new es.board.repository.entity.PointHistoryEntity(u.username, sum(p.pointChange))" +
            " FROM PointHistoryEntity p" +
            " inner join User u where u.userId=p.userId" +
            " GROUP BY u.username")
    List<PointHistoryEntity> findByUserAllId();

}
