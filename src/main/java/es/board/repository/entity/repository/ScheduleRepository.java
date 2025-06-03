package es.board.repository.entity.repository;

import es.board.repository.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByUserId(String userId); // 특정 사용자의 일정 조회

    @Query("SELECT t FROM Schedule t WHERE t.userId = :userId")
    List<Schedule> findAllBySchedule(@Param("userId") String userId);


    @Query(value = """
    SELECT * FROM (
        SELECT *, ROW_NUMBER() OVER (PARTITION BY title, repeat_start_date ORDER BY start_datetime ASC) AS rn
        FROM schedule
        WHERE user_id = :userId AND is_repeat = true
    ) t WHERE t.rn = 1
""", nativeQuery = true)
    List<Schedule> findDistinctRepeatSchedules(@Param("userId") String userId);



    @Modifying
    @Transactional
    @Query("DELETE FROM Schedule t WHERE t.userId = :userId AND t.createdAt = :createdAt AND t.isRepeat = true")
    void deleteByUserIdAndCreatedAtAndIsRepeat(@Param("userId") String userId, @Param("createdAt") LocalDateTime createdAt);

}