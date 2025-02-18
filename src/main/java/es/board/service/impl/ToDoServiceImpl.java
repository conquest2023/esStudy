package es.board.service.impl;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.mapper.ToDoMapper;
import es.board.controller.model.req.TodoRequest;
import es.board.controller.model.res.TodoResponse;
import es.board.repository.entity.Todo;
import es.board.repository.entity.TodoStatus;
import es.board.repository.entity.entityrepository.TodoRepository;
import es.board.service.NotificationService;
import es.board.service.ToDoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class ToDoServiceImpl implements ToDoService {

    private  final TodoRepository todoRepository;

    private  final JwtTokenProvider jwtTokenProvider;

    private  final ToDoMapper toDoMapper;

    private final NotificationService notificationService;

    private final RedisTemplate<String, Object> redisTemplate;

    private final String REDIS_TODO_COUNT_KEY = "todo_count:";

    @Override
    public List<TodoRequest> getUserToDo(String userId) {
        return toDoMapper.EntityToTodo(todoRepository.findTodayTodos(userId,LocalDate.now()));
    }

    @Override
    public void addToDo(String token, TodoResponse todoResponse) {

        todoRepository.save(toDoMapper.TodoToEntity(jwtTokenProvider.getUserId(token), todoResponse));
        String redisKey = REDIS_TODO_COUNT_KEY + jwtTokenProvider.getUserId(token);
        redisTemplate.opsForValue().increment(redisKey);
        updateTodoCache(jwtTokenProvider.getUserId(token));

    }

    @Override
    public Object getRemainingTodos(String userId) {
        String redisKey = REDIS_TODO_COUNT_KEY + userId;

        Object cachedCount = redisTemplate.opsForValue().get(redisKey);
        if (cachedCount != null) {
            return cachedCount;
        }
        Long remainingCount = todoRepository.countByUserIdAndStatus(userId);
        redisTemplate.opsForValue().set(redisKey, remainingCount, Duration.ofSeconds(60));

        notificationService.sendTodoNotification(userId, " 남은 Todo: " + remainingCount + "개");


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
        Long newCount = todoRepository.countByUserIdAndStatus(userId);
        redisTemplate.opsForValue().set(redisKey, newCount, Duration.ofSeconds(60));
    }

    @Override
    public void completeTodo(String token, Long id) {

        todoRepository.updateStatus(TodoStatus.DONE,id);
        String redisKey = REDIS_TODO_COUNT_KEY + jwtTokenProvider.getUserId(token);
        redisTemplate.opsForValue().decrement(redisKey);
        updateTodoCache(jwtTokenProvider.getUserId(token));
        // ✅ 현재 남은 Todo 개수 가져오기
        Object remainingCount = redisTemplate.opsForValue().get(redisKey);
        notificationService.sendTodoNotification(jwtTokenProvider.getUserId(token), "✅ Todo 완료! 남은 Todo: " + remainingCount + "개");
    }

    @Override
    public Long getDoneTodo(String userId) {
       return   todoRepository.countByUserId(userId,LocalDate.now());
    }
    @Scheduled(fixedRate = 600000) // 10분마다 실행
    public void syncRedisWithDatabase() {
        List<String> allUserIds = todoRepository.findAllUserIds();
        for (String userId : allUserIds) {
            Long remainingCount = todoRepository.countByUserIdAndStatus(userId);
            redisTemplate.opsForValue().set(REDIS_TODO_COUNT_KEY + userId, remainingCount, Duration.ofSeconds(60));
        }
    }
}
