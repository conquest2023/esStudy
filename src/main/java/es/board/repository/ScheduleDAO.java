package es.board.repository;

import es.board.repository.document.Schedule;

import java.time.LocalDateTime;
import java.util.List;


public interface ScheduleDAO {


    void deleteRepeatSchedule(String userId, LocalDateTime start,LocalDateTime end);
    void deleteSchedule(Long id);
    void saveScheduleBulk(List<Schedule> schedules);
    void saveSchedule(Schedule schedule);

     List<Schedule> searchSchedule(String userId, String query, String searchType, String sortType);

}
