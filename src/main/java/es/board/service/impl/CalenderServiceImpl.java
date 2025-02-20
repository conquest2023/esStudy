package es.board.service.impl;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.mapper.ToDoMapper;
import es.board.controller.model.req.ScheduleDTO;
import es.board.repository.entity.entityrepository.ScheduleRepository;
import es.board.service.CalenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class CalenderServiceImpl implements CalenderService {

    private  final ScheduleRepository   scheduleRepository;

    private  final JwtTokenProvider jwtTokenProvider;

    private  final ToDoMapper toDoMapper;
    @Override
    public void saveSchedule(String token ,ScheduleDTO scheduleDTO) {
        scheduleRepository.save(toDoMapper.toSchedule(jwtTokenProvider.getUserId(token),scheduleDTO));
    }

    @Override
    public void deleteSchedule(Long id, String token) {
        scheduleRepository.deleteById(id);
    }

    @Override
    public List<ScheduleDTO> getSchedule(String token) {
        return toDoMapper.fromSchedule(scheduleRepository.findAllBySchedule(jwtTokenProvider.getUserId(token)));
    }
}
