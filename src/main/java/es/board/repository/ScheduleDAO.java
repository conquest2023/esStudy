package es.board.repository;

import es.board.controller.model.req.ScheduleDTO;
import es.board.repository.document.Board;
import es.board.repository.document.Schedule;
import es.board.repository.document.Todo;

import java.time.LocalDateTime;
import java.util.List;


public interface ScheduleDAO {


    void deleteRepeatSchedule(String userId, LocalDateTime start,LocalDateTime end);
    void deleteSchedule(Long id);
    void saveScheduleBulk(List<Schedule> schedules);
    void saveSchedule(Schedule schedule);

     List<Schedule> searchSchedule(String userId, String query, String searchType, String sortType);

}
