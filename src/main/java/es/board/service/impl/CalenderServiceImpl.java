package es.board.service.impl;

import es.board.config.jwt.JwtTokenProvider;
import es.board.mapper.MainFunctionMapper;
import es.board.controller.model.dto.todo.ScheduleDTO;
import es.board.repository.ScheduleDAO;
import es.board.repository.entity.PointHistoryEntity;
import es.board.repository.entity.Schedule;
import es.board.repository.entity.repository.PointHistoryRepository;
import es.board.repository.entity.repository.ScheduleRepository;
import es.board.service.CalenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
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

    private  final StringRedisTemplate stringRedisTemplate;

    private final PointHistoryRepository pointHistoryRepository;

    private  final MainFunctionMapper toDoMapper;

    @Override
    public void saveRepeatSchedule(String token, ScheduleDTO scheduleDTO) {
        CompletableFuture.runAsync(() -> {


            List<Schedule> schedulesToInsert = new ArrayList<>(toDoMapper.generateRepeatSchedules(jwtTokenProvider.getUserId(token), scheduleDTO));
            log.info(schedulesToInsert.toString());
            List<Schedule> savedSchedules = scheduleRepository.saveAll(schedulesToInsert);
            List<es.board.repository.document.Schedule> scheduleDocuments = toDoMapper.toScheduleDocumentList(jwtTokenProvider.getUserId(token), savedSchedules);
            asyncService.saveScheduleBulkAsync(scheduleDocuments);
        });
        grantCalendarPoint(jwtTokenProvider.getUserId(token),jwtTokenProvider.getUsername(token));
    }

    @Override
    public void saveSchedule(String token , ScheduleDTO scheduleDTO) {

         CompletableFuture.runAsync(() -> {
            Long saveScheduleId = getSaveScheduleId(token,scheduleDTO);
             asyncService.saveScheduleAsync((toDoMapper.toScheduleDocument(jwtTokenProvider.getUserId(token),scheduleDTO,saveScheduleId)));
         });
        grantCalendarPoint(jwtTokenProvider.getUserId(token),jwtTokenProvider.getUsername(token));

    }

    @Override
    public List<ScheduleDTO> getRepeatSchedule(String token) {
        return  toDoMapper.fromSchedule(scheduleRepository.findDistinctRepeatSchedules(jwtTokenProvider.getUserId(token)));
    }

    @Override
    public List<ScheduleDTO> searchSchedule(String token, String title, String  description, String  category) {
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

    public void grantCalendarPoint(String userId,String username) {
        String today = LocalDate.now().toString();
        String key = "calendar:point:" + userId + ":" + today;
        Long currentCount = stringRedisTemplate.opsForValue().increment(key);
        if (currentCount == 1) {
            stringRedisTemplate.expire(key, Duration.ofDays(1));
        }
        if (currentCount > 3) {
            log.info("{}님은 오늘 캘린더 작성 포인트 한도를 초과했습니다.", userId);
            return;
        }
        createPointHistory(userId);
        log.info("캘린더 작성 포인트 지급 완료! 현재 작성 횟수: {}", currentCount);
    }

    public void createPointHistory(String userId) {
        PointHistoryEntity history = PointHistoryEntity.builder()
                .userId(userId)
                .pointChange(5)
//                .username(username)
                .reason("캘린더")
                .createdAt(LocalDateTime.now())
                .build();
        pointHistoryRepository.save(history);
    }
}
