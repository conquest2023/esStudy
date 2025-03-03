package es.board.repository.entity.entityrepository;

import es.board.repository.entity.CertificationSchedule;
import es.board.repository.entity.Likes;
import es.board.repository.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CertificationScheduleRepository  extends JpaRepository<CertificationSchedule,String> {

    @Query("SELECT t FROM CertificationSchedule t WHERE t.majorCategory = :majorCategory")
    List<CertificationSchedule> findAllByCertificationSchedule(@Param("majorCategory") String majorCategory);
}
