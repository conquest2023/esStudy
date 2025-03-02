package es.board.repository.entity.entityrepository;

import es.board.repository.entity.CertificationSchedule;
import es.board.repository.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CertificationScheduleRepository  extends JpaRepository<CertificationSchedule,String> {
}
