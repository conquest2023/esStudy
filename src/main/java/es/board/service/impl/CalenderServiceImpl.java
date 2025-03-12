package es.board.service.impl;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.mapper.MainFunctionMapper;
import es.board.controller.model.req.ScheduleDTO;
import es.board.repository.ScheduleDAO;
import es.board.repository.entity.Schedule;
import es.board.repository.entity.entityrepository.ScheduleRepository;
import es.board.service.CalenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
@Slf4j
public class CalenderServiceImpl implements CalenderService {

    private  final ScheduleRepository   scheduleRepository;

    private  final JwtTokenProvider jwtTokenProvider;

    private final AsyncService asyncService;

    private  final ScheduleDAO scheduleDAO;

    private  final MainFunctionMapper toDoMapper;

    @Override
    public void saveRepeatSchedule(String token, ScheduleDTO scheduleDTO) {
        CompletableFuture.supplyAsync(() -> {

            List<Schedule> schedulesToInsert = new ArrayList<>(toDoMapper.generateRepeatSchedules(jwtTokenProvider.getUserId(token), scheduleDTO));

            List<Schedule> savedSchedules = scheduleRepository.saveAll(schedulesToInsert);


            List<es.board.repository.document.Schedule> scheduleDocuments = toDoMapper.toScheduleDocumentList(jwtTokenProvider.getUserId(token), savedSchedules);

            asyncService.saveScheduleBulkAsync(scheduleDocuments);

            return null;
        });
    }

    @Override
    public void saveSchedule(String token ,ScheduleDTO scheduleDTO) {

         CompletableFuture.supplyAsync(() -> {
            Long saveScheduleId = getSaveScheduleId(token,scheduleDTO);
             asyncService.saveScheduleAsync((toDoMapper.toScheduleDocument(jwtTokenProvider.getUserId(token),scheduleDTO,saveScheduleId)));
             return null;
         });
    }

    @Override
    public List<ScheduleDTO> getRepeatSchedule(String token) {
        return  toDoMapper.fromSchedule(scheduleRepository.findDistinctRepeatSchedules(jwtTokenProvider.getUserId(token)));
    }

    @Override
    public List<ScheduleDTO> searchSchedule(String token,String title,String  description, String  category) {
        return toDoMapper.fromScheduleDocument(scheduleDAO.searchSchedule(jwtTokenProvider.getUserId(token),title,description,category));
    }

    @Override
    public void deleteSchedule(Long id, String token) {

        scheduleRepository.deleteById(id);

        CompletableFuture.runAsync(() -> {
            scheduleDAO.deleteSchedule(id);
        });
    }

    @Override
    public void deleteRepeatSchedule(String token, LocalDateTime createdAt, LocalDateTime start, LocalDateTime end) {
        scheduleRepository.deleteByUserIdAndCreatedAtAndIsRepeat(jwtTokenProvider.getUserId(token),createdAt);

        CompletableFuture.runAsync(() -> {
                scheduleDAO.deleteRepeatSchedule(jwtTokenProvider.getUserId(token),start,end);
            });
        }

    @Override
    public List<ScheduleDTO> getSchedule(String token) {
        return toDoMapper.fromSchedule(scheduleRepository.findAllBySchedule(jwtTokenProvider.getUserId(token)));
    }

    private Long getSaveScheduleId(String token, ScheduleDTO scheduleDTO) {


        Schedule savedPost = scheduleRepository.save(toDoMapper.toScheduleEntity(jwtTokenProvider.getUserId(token),scheduleDTO));

        return savedPost.getScheduleId();
    }
}
