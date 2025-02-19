package es.board;

import es.board.repository.ToDoDAO;
import es.board.repository.document.Todo;
import es.board.repository.entity.entityrepository.TodoRepository;
import es.board.service.ToDoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Component
@SpringBootTest
public class ScheduledJobTester {

    @Autowired private TodoRepository todoRepository;
    @Autowired private ToDoDAO toDoDAO;
    @Test
    public void findAllUser() {
        System.out.println("🚀 Todo 완료율 계산 시작...");

        Set<String> userIds = todoRepository.findSETAllTodoUserTodayIds(LocalDate.now().minusDays(1));
        
        System.out.println("userIds = " + userIds);
        System.out.println("LocalDate.now().minusDays(1) = " + LocalDate.now().minusDays(1));
    }

    @Test
    public void calculateCompletionRates() {
        System.out.println("🚀 Todo 완료율 계산 시작...");

        Set<String> userIds = todoRepository.findSETAllTodoUserTodayIds(LocalDate.now().minusDays(1));

        System.out.println("userIds = " + userIds);

        List<Todo> completionRates = new ArrayList<>();

        for (String userId : userIds) {
            long totalTodos = todoRepository.countGraphByUserAllId(userId, LocalDate.now().minusDays(1));
            long completedTodos = todoRepository.countByUserIdClearToDo(userId,LocalDate.now().minusDays(1));
            double completionRate = totalTodos > 0 ? (double) completedTodos / totalTodos * 100 : 0;

            System.out.println("totalTodos = " + totalTodos);
            System.out.println("completedTodos = " + completedTodos);
            System.out.println("completionRate = " + completionRate);
            System.out.println(" 전체= " +todoRepository.countGraphByUserAllId(userId, LocalDate.now()));
            System.out.println("클리어 = " + todoRepository.countByUserIdClearToDo(userId,LocalDate.now()));
            Todo rate = new Todo(userId, completionRate, LocalDateTime.now());
            completionRates.add(rate);
        }
        toDoDAO.savePercentTodo(completionRates);
        System.out.println(completionRates.size());
    }
    @Test
    public void calculateAndStoreCompletionRates() {
        System.out.println("🚀 Todo 완료율 계산 시작...");

        List<String> userIds = todoRepository.findAllTodoUserTodayIds(LocalDate.now().minusDays(1));

        List<Todo> completionRates = new ArrayList<>();

        for (String userId : userIds) {
            long totalTodos = todoRepository.countGraphByUserAllId(userId, LocalDate.now());
            long completedTodos = todoRepository.countByUserIdClearToDo(userId,LocalDate.now());
            double completionRate = totalTodos > 0 ? (double) completedTodos / totalTodos * 100 : 0;

            Todo rate = new Todo(userId, completionRate, LocalDateTime.now());
            completionRates.add(rate);
        }
        // ✅ Elasticsearch에 저장
        toDoDAO.savePercentTodo(completionRates);
        System.out.println(completionRates.size());
    }


}
