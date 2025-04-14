package es.board.repository.entity.entityrepository;

import es.board.repository.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository  extends JpaRepository<Notification,Long> {

}
