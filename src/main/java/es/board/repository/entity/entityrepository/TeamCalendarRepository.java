package es.board.repository.entity.entityrepository;

import es.board.repository.entity.TeamSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamCalendarRepository extends JpaRepository<TeamSchedule, Long> {
}
