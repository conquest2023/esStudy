package es.board.service;

import es.board.controller.model.req.ScheduleRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface CalenderService {


    void saveRepeatSchedule(String token, ScheduleRequest scheduleDTO);


    void saveSchedule(String token, ScheduleRequest scheduleDTO);

    List<ScheduleRequest> getRepeatSchedule(String token);


    List<ScheduleRequest> searchSchedule(String token, String title, String  description, String  category);

    void  deleteSchedule(Long id ,String token);


    void  deleteRepeatSchedule(String token, LocalDateTime createdAt,LocalDateTime start, LocalDateTime end);

    List<ScheduleRequest> getSchedule(String token);
}
