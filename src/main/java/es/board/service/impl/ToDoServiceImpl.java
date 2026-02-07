package es.board.service.impl;

import es.board.domain.todo.Todo;
import es.board.controller.model.dto.todo.TodoDTO;
import es.board.infrastructure.entity.todo.TodoEntity;
import es.board.mapper.TodoMapper;
import es.board.repository.entity.repository.TodoRepository;
import es.board.service.ToDoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
@Slf4j
@RequiredArgsConstructor
public class ToDoServiceImpl implements ToDoService {

    private final String REDIS_TODO_COUNT_KEY = "todo_count:";

//    private final NotificationService notificationService;

    private  final TodoRepository todoRepository;

    private final RedisTemplate<String, Object> redisTemplate;



    @Override
    public List<TodoDTO.Response> getTodayTodos(String userId) {
        return TodoMapper.toTodoList(todoRepository.findTodayTodos(userId,LocalDate.now()));
    }

    @Override
    public List<TodoDTO.Response> getAllTodos(String userId) {
        return  TodoMapper.toTodoList(todoRepository.findAllByTodos(userId));
    }


    @Override
    public void addTodo(String userId, TodoDTO.Request request) {
//        TodoEntity entity = TodoMapper.toEntity(userId, request);
//
//        todoRepository.save();
//        String redisKey = REDIS_TODO_COUNT_KEY +userId;
//        redisTemplate.opsForValue().increment(redisKey);
//        updateTodoCache(userId);
//        grantTodoPoint(jwtTokenProvider.getUserId(userId),jwtTokenProvider.getUsername(userId));
    }

    @Override
    @Transactional
    public void finishTodo(String userId, Long id) {

        TodoEntity todoEntity = todoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Todo를 찾을 수 없습니다."));

        todoEntity.complete();
//        String redisKey = REDIS_TODO_COUNT_KEY   +userId;
//        redisTemplate.opsForValue().decrement(redisKey);
//        updateTodoCache(userId);
//        Object remainingCount = redisTemplate.opsForValue().get(redisKey);
//        notificationService.sendTodoNotification(userId, "Todo 완료! 남은 Todo: " + remainingCount + "개");
    }
    @Override
    public void deleteTodo(Long id, String userId) {
        todoRepository.deleteById(id);
//        String redisKey = REDIS_TODO_COUNT_KEY + userId;
//        redisTemplate.opsForValue().decrement(redisKey);
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
//            notificationService.sendTodoNotification(userId, " 남은 TodoEntity: " + remainingCount + "개");
        }
        return remainingCount;
    }



    @Override
    public Long getDoneTodo(String userId) {
       return   todoRepository.countByUserIdClearTodo(userId,LocalDate.now());
    }

    @Override
    public void addProjectTodo(String userId, TodoDTO.Request todoResponse, String username) {
        todoRepository.save((TodoMapper.toEntity(userId,todoResponse)));
    }

    @Override
    public List<TodoDTO.Response> getProjectTodo(String userId) {
        return  null;
//      return TodoMapper.entityToTodo(todoRepository.findProjectTodo(userId));
    }

    @Override
    public void updateTodoCache(String userId) {
        String redisKey = REDIS_TODO_COUNT_KEY + userId;
        Long newCount = todoRepository.countByUserIdAndStatusYetTodo(userId,LocalDate.now());
        redisTemplate.opsForValue().set(redisKey, newCount, Duration.ofSeconds(60));
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
            log.info("TodoEntity 완료율 계산 시작");

            Set<String> userIds = todoRepository.findSETAllTodoUserTodayIds(LocalDate.now()); // 모든 사용자 ID 조회
            List<Todo> completionRates = new ArrayList<>();

            for (String userId : userIds) {
                long totalTodos = todoRepository.countGraphByUserAllId(userId,LocalDate.now());
                long completedTodos = todoRepository.countByUserIdClearTodo(userId,LocalDate.now());
                double completionRate = totalTodos > 0 ? (double) completedTodos / totalTodos * 100 : 0;

//                Todo rate = new Todo(userId, completionRate, LocalDateTime.now());
//                completionRates.add(rate);
            }

//            toDoDAO.savePercentTodo(completionRates);
            log.info("TodoEntity 완료율 저장 완료! 저장된 개수: {}", completionRates.size());
        }

//        public void grantTodoPoint(String userId,String username) {
//            String today = LocalDate.now().toString();
//            String key = "todo:point:" + userId + ":" + today;
//            Long currentCount = stringRedisTemplate.opsForValue().increment(key);
//            if (currentCount == 1) {
//                stringRedisTemplate.expire(key, Duration.ofDays(1));
//            }
//            if (currentCount > 3) {
//                log.info("{}님은 오늘 TodoEntity 작성 포인트 한도를 초과했습니다.", userId);
//                return;
//            }
//            createPointHistory(userId);
//            log.info("TodoEntity 작성 포인트 지급 완료! 현재 작성 횟수: {}", currentCount);
//        }

//        public void createPointHistory(String userId) {
//            PointHistoryEntity history = PointHistoryEntity.builder()
//                    .userId(userId)
////                    .username(username)
//                    .pointChange(5)
//                    .reason("TodoEntity")
//                    .createdAt(LocalDateTime.now())
//                    .build();
////            pointHistoryRepository.save(history);
//        }
}
