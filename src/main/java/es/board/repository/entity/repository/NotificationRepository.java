package es.board.repository.entity.repository;

import es.board.repository.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository  extends JpaRepository<Notification,Long> {
    @Query("select u from Notification u where u.userId = :userId order by u.createdAt desc")
    List<Notification> findByNotificationList(@Param("userId") String userId);



    @Query("select u from Notification u " +
            "where u.userId = :userId " +
            "  and u.isCheck = false " +
            "  and u.createdAt >= :sevenDaysAgo " +
            "order by u.createdAt desc")
    List<Notification> findByRecentNotification(
            @Param("userId") String userId, @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);

    @Query("select count(*) from Notification u where u.userId = :userId and u.isCheck=false")
    int countByIsCheckNotification(@Param("userId") String userId);

    @Modifying
    @Transactional
    @Query("delete  from Notification u  where u.notificationId in :notificationId  and  u.userId = :userId ")
    void deleteById(@Param("userId") String userId, @Param("notificationId") List<Long> notificationId);


    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isCheck = true ,n.updatedAt=:updatedAt  where n.notificationId in :notificationId AND n.userId = :userId")
    void checkNotification(@Param("userId") String userId,
                           @Param("notificationId") List<Long> notificationId,
                           @Param("updatedAt")LocalDateTime updatedAt);
}
