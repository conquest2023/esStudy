package es.board.repository.entity.entityrepository;

import es.board.repository.entity.Schedule;
import es.board.repository.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByUserId(String userId); // 특정 사용자의 일정 조회

    @Query("SELECT t FROM Schedule t WHERE t.userId = :userId")
    List<Schedule> findAllBySchedule(@Param("userId") String userId);
}