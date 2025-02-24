package es.board.service;

import es.board.controller.model.req.ScheduleDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface CalenderService {


    void saveRepeatSchedule(String token,ScheduleDTO scheduleDTO);


    void saveSchedule(String token, ScheduleDTO scheduleDTO);

    List<ScheduleDTO> getRepeatSchedule(String token);


    List<ScheduleDTO> searchSchedule(String token,String title,String  description, String  category);

    void  deleteSchedule(Long id ,String token);


    void  deleteRepeatSchedule(String token, LocalDateTime createdAt,LocalDateTime start, LocalDateTime end);

    List<ScheduleDTO> getSchedule(String token);
}
