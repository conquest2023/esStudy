package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.req.TodoRequest;
import es.board.controller.model.res.TodoResponse;
import es.board.service.ToDoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequiredArgsConstructor
@Slf4j
public class ToDoController {

    private  final JwtTokenProvider jwtTokenProvider;


    private  final ToDoService toDoService;

    @GetMapping("/events")
    public List<Map<String, Object>> getEvents() {
        List<Map<String, Object>> events = new ArrayList<>();

        Map<String, Object> event1 = new HashMap<>();
        event1.put("title", "Spring Boot 미팅");
        event1.put("start", "2025-02-20T10:00:00");

        Map<String, Object> event2 = new HashMap<>();
        event2.put("title", "프로젝트 데드라인");
        event2.put("start", "2025-02-25");

        events.add(event1);
        events.add(event2);

        return events;
    }

    @GetMapping("/search/todo")
    @ResponseBody
    public ResponseEntity<?> getTodoById(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }

        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }

        List<TodoRequest> todos = toDoService.getUserToDo(jwtTokenProvider.getUserId(token));
        Long completedCount = toDoService.getDoneTodo(jwtTokenProvider.getUserId(token));
        log.info("asdas");

        // ✅ JSON 형식으로 반환 (Todo 목록 + 완료 개수)
        Map<String, Object> response = new HashMap<>();
        response.put("todos", todos);
        response.put("completedCount", completedCount);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/alltodo")
    @ResponseBody
    public ResponseEntity<?> getTodoAll(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }

        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }

        List<TodoRequest> todos = toDoService.getUserAllToDo(jwtTokenProvider.getUserId(token));
        log.info(todos.toString());
        // ✅ JSON 형식으로 반환 (Todo 목록 + 완료 개수)
        Map<String, Object> response = new HashMap<>();
        response.put("todos", todos);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/todo/remaining")
    @ResponseBody
    public ResponseEntity<?> getRemainingTodos(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }

        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }
        String userId = jwtTokenProvider.getUserId(token);
        Object remainingCount = toDoService.getRemainingTodos(userId);

        return ResponseEntity.ok(Map.of("remainingCount", remainingCount));
    }



    @PostMapping("/save/todo")
    public void saveTodo(@RequestHeader(value = "Authorization") String token, @RequestBody TodoResponse todoResponse) {

        log.info(todoResponse.toString());
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                toDoService.addToDo(token,todoResponse);
            }
        }
//        return todoRepository.findById(id).orElseThrow(() -> new RuntimeException("Todo Not Found"));
    }

    @PostMapping("/todo/delete/{id}")
    public void deleteTodo(@PathVariable Long id,@RequestHeader(value = "Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                toDoService.deleteToDo(id,token);
            }
        }
    }

    @PutMapping("/todo/status/{id}")
    public ResponseEntity<?> updateTodoStatus(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization") String token) {

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }

        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }
        toDoService.completeTodo(token,id);
        return ResponseEntity.ok(Map.of("message", "Todo 상태가 DONE으로 변경되었습니다."));
        }
    }


