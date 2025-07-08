package es.board.service.impl;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.mapper.MainFunctionMapper;
import es.board.controller.model.dto.todo.D_DayDTO;
import es.board.controller.model.dto.todo.TodoDTO;
import es.board.repository.ToDoDAO;
import es.board.repository.document.Todo;
import es.board.repository.entity.PointHistory;
import es.board.repository.entity.TodoStatus;
import es.board.repository.entity.repository.D_DayRepository;
import es.board.repository.entity.repository.PointHistoryRepository;
import es.board.repository.entity.repository.TodoRepository;
import es.board.service.NotificationService;
import es.board.service.ToDoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
@Slf4j
@RequiredArgsConstructor
public class ToDoServiceImpl implements ToDoService {

    private  final TodoRepository todoRepository;

    private  final PointHistoryRepository pointHistoryRepository;

    private  final StringRedisTemplate stringRedisTemplate;

    private  final JwtTokenProvider jwtTokenProvider;

    private  final ToDoDAO toDoDAO;

    private  final MainFunctionMapper toDoMapper;

    private final NotificationService notificationService;

    private final RedisTemplate<String, Object> redisTemplate;

    private final String REDIS_TODO_COUNT_KEY = "todo_count:";


    private final D_DayRepository dayRepository;


    @Override
    public List<TodoDTO.Request> getUserToDo(String userId) {
        return toDoMapper.EntityToTodo(todoRepository.findTodayTodos(userId,LocalDate.now()));
    }

    @Override
    public List<TodoDTO.Request> getUserAllToDo(String userId) {
        return  toDoMapper.EntityToTodo(todoRepository.findAllByTodos(userId));
    }

    @Override
    public void addToDo(String token, TodoDTO.Response todoResponse) {
        todoRepository.save(toDoMapper.TodoToEntity(jwtTokenProvider.getUserId(token), todoResponse));
        String redisKey = REDIS_TODO_COUNT_KEY + jwtTokenProvider.getUserId(token);
        redisTemplate.opsForValue().increment(redisKey);
        updateTodoCache(jwtTokenProvider.getUserId(token));
        grantTodoPoint(jwtTokenProvider.getUserId(token),jwtTokenProvider.getUsername(token));
    }

    @Override
    public void addD_Day(String token, D_DayDTO dDayDTO) {
        dayRepository.save(toDoMapper.toEntityD_Day(jwtTokenProvider.getUserId(token),dDayDTO));
        grantTodoPoint(jwtTokenProvider.getUserId(token),jwtTokenProvider.getUsername(token));
    }

    @Override
    public void delete_D_Day(Long id) {
        dayRepository.deleteById(id);
    }

    @Override
    public List<D_DayDTO> getD_Day(String token) {

      return toDoMapper.fromD_DayEntityList(dayRepository.findAll(jwtTokenProvider.getUserId(token)));
    }

    @Override
    public Object getRemainingTodos(String userId) {
        String redisKey = REDIS_TODO_COUNT_KEY + userId;
        Object cachedCount = redisTemplate.opsForValue().get(redisKey);
        if (cachedCount != null) {
            return cachedCount;
        }
        Long remainingCount = todoRepository.countByUserIdAndStatusYetTodo(userId,LocalDate.now());
        if(remainingCount>0) {
            redisTemplate.opsForValue().set(redisKey, remainingCount, Duration.ofSeconds(60));
            notificationService.sendTodoNotification(userId, " 남은 Todo: " + remainingCount + "개");
        }
        return remainingCount;
    }

    @Override
    public void deleteToDo(Long id,String token) {
        todoRepository.deleteById(id);
        String redisKey = REDIS_TODO_COUNT_KEY + jwtTokenProvider.getUserId(token);
        redisTemplate.opsForValue().decrement(redisKey);
    }

    @Override
    public void updateTodoCache(String userId) {
        String redisKey = REDIS_TODO_COUNT_KEY + userId;
        Long newCount = todoRepository.countByUserIdAndStatusYetTodo(userId,LocalDate.now());
        redisTemplate.opsForValue().set(redisKey, newCount, Duration.ofSeconds(60));
    }

    @Override
    public void completeTodo(String token, Long id) {
        todoRepository.updateStatus(TodoStatus.DONE,id);
        String redisKey = REDIS_TODO_COUNT_KEY + jwtTokenProvider.getUserId(token);
        redisTemplate.opsForValue().decrement(redisKey);
        updateTodoCache(jwtTokenProvider.getUserId(token));
        Object remainingCount = redisTemplate.opsForValue().get(redisKey);
        notificationService.sendTodoNotification(jwtTokenProvider.getUserId(token), "Todo 완료! 남은 Todo: " + remainingCount + "개");
    }

    @Override
    public Long getDoneTodo(String userId) {
       return   todoRepository.countByUserIdClearTodo(userId,LocalDate.now());
    }

    @Override
    public void addProjectTodo(String userId, TodoDTO.Response todoResponse, String username) {
        todoRepository.save(toDoMapper.TodoToEntity(userId,todoResponse));
        grantTodoPoint(userId,username);
    }

    @Override
    public List<TodoDTO.Request> getProjectTodo(String userId) {
      return  toDoMapper.EntityToTodo(todoRepository.findProjectTodo(userId));
    }

    @Scheduled(fixedRate = 1000000)
    public void syncRedisWithDatabase() {
        Set<String> allUserIds = todoRepository.findSETAllTodoUserTodayIds(LocalDate.now());
        log.info(allUserIds.toString());
        for (String userId : allUserIds) {
            Long remainingCount = todoRepository.countByUserIdAndStatusYetTodo(userId,LocalDate.now());
            redisTemplate.opsForValue().set(REDIS_TODO_COUNT_KEY + userId, remainingCount, Duration.ofSeconds(60));
        }
    }
        @Scheduled(cron = "0 0 15 * * *", zone = "Asia/Seoul")
        public void calculateAndStoreCompletionRates() {
            log.info("Todo 완료율 계산 시작");

            Set<String> userIds = todoRepository.findSETAllTodoUserTodayIds(LocalDate.now()); // 모든 사용자 ID 조회
            List<Todo> completionRates = new ArrayList<>();

            for (String userId : userIds) {
                long totalTodos = todoRepository.countGraphByUserAllId(userId,LocalDate.now());
                long completedTodos = todoRepository.countByUserIdClearTodo(userId,LocalDate.now());
                double completionRate = totalTodos > 0 ? (double) completedTodos / totalTodos * 100 : 0;

                Todo rate = new Todo(userId, completionRate, LocalDateTime.now());
                completionRates.add(rate);
            }

            toDoDAO.savePercentTodo(completionRates);
            log.info("Todo 완료율 저장 완료! 저장된 개수: {}", completionRates.size());
        }

        public void grantTodoPoint(String userId,String username) {
            String today = LocalDate.now().toString();
            String key = "todo:point:" + userId + ":" + today;
            Long currentCount = stringRedisTemplate.opsForValue().increment(key);
            if (currentCount == 1) {
                stringRedisTemplate.expire(key, Duration.ofDays(1));
            }
            if (currentCount > 3) {
                log.info("{}님은 오늘 Todo 작성 포인트 한도를 초과했습니다.", userId);
                return;
            }
            createPointHistory(userId,username);
            log.info("Todo 작성 포인트 지급 완료! 현재 작성 횟수: {}", currentCount);
        }

        public void createPointHistory(String userId,String username) {
            PointHistory history = PointHistory.builder()
                    .userId(userId)
                    .username(username)
                    .pointChange(5)
                    .reason("Todo")
                    .createdAt(LocalDateTime.now())
                    .build();
            pointHistoryRepository.save(history);
        }
}
