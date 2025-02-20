package es.board.service;

import es.board.controller.model.req.ScheduleDTO;

import java.util.List;

public interface CalenderService {



    void saveSchedule(String token, ScheduleDTO scheduleDTO);



    List<ScheduleDTO> getSchedule(String token);
}
