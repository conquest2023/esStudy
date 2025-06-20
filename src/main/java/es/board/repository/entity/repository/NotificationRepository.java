package es.board.repository.entity.repository;

import es.board.repository.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository  extends JpaRepository<Notification,Long> {
    @Query("select u from Notification u where u.userId = :userId order by u.createdAt desc")
    List<Notification> findByNotificationList(@Param("userId") String userId);
}
