package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.dto.todo.TodoDTO;
import es.board.ex.TokenValidator;
import es.board.service.ToDoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")

public class ToDoController {

    private final JwtTokenProvider jwtTokenProvider;

    private  final TokenValidator tokenValidator;

    private final ToDoService toDoService;

//    @GetMapping("/elastic/schedule")
//    @ResponseBody
//    public ResponseEntity<?> searchTodoById(@RequestHeader(value = "Authorization", required = false) String token) {
//        if (token == null || !token.startsWith("Bearer ")) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
//        }
//
//        token = token.substring(7);
//        if (!jwtTokenProvider.validateToken(token)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
//        }
//
//        List<TodoRequest> todos = toDoService.getUserToDo(jwtTokenProvider.getUserId(token));
//        Long completedCount = toDoService.getDoneTodo(jwtTokenProvider.getUserId(token));
//        log.info("asdas");
//        Map<String, Object> response = new HashMap<>();
//        response.put("todos", todos);
//        response.put("completedCount", completedCount);
//
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/todo")
    public void saveTodo(@RequestAttribute("userId") String userId,
                         @RequestBody TodoDTO.Request request) {
        toDoService.addTodo(userId,request);
    }

    @DeleteMapping("/todo/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long id, @RequestAttribute("userId") String userId) {

        toDoService.deleteTodo(id, userId);

        return ResponseEntity.ok("삭제되었습니다");
    }

    @PutMapping("/todo/{id}/complete")
    public ResponseEntity<?> updateTodoStatus(
            @PathVariable Long id,
            @RequestAttribute("userId") String userId) {

        toDoService.finishTodo(userId, id);
        return ResponseEntity.ok(Map.of("message", "Todo 상태가 DONE으로 변경되었습니다."));
    }


    @GetMapping("/todo")
    @ResponseBody
    public ResponseEntity<?> getTodos(@RequestAttribute("userId") String userId) {

        return ResponseEntity.ok(Map.of("todos",toDoService.getTodayTodos(userId),
                "completedCount", toDoService.getDoneTodo(userId)));
    }


    @GetMapping("/search/today/todo")
    public ResponseEntity<?> getTodoByToday(@RequestHeader(value = "Authorization", required = false) String token) {
        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
        if (tokenCheckResponse == null) {
            return tokenCheckResponse;
        }
        token=token.substring(7);
        String userId=jwtTokenProvider.getUserId(token);
        return ResponseEntity.ok(Map.of("remainingCount", toDoService.getRemainingTodos(userId)
            ,"todos", toDoService.getRemainingTodos(userId)));
    }

    @GetMapping("/todos")
    @ResponseBody
    public ResponseEntity<?> getTodoAll(@RequestAttribute("userId") String userId) {

        return ResponseEntity.ok(Map.of("todos",toDoService.getAllTodos(userId)));
    }

    @GetMapping("/todo/remaining")
    @ResponseBody
    public ResponseEntity<?> getRemainingTodos(@RequestHeader("Authorization") String token) {
        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
        if (tokenCheckResponse == null) {
            return tokenCheckResponse;
        }
        token=token.substring(7);
        String userId = jwtTokenProvider.getUserId(token);
        return ResponseEntity.ok(Map.of("remainingCount", toDoService.getRemainingTodos(userId)));
    }

    @PostMapping("/save/project/todo")
    public void saveProjectToDo(@RequestHeader(value = "Authorization") String token, @RequestBody TodoDTO.Request todoResponse) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                toDoService.addProjectTodo(jwtTokenProvider.getUserId(token), todoResponse,jwtTokenProvider.getUsername(token));
            }
        }
    }
    @GetMapping("/search/project/todo")
    public ResponseEntity<?> getProjectToDo(@RequestHeader(value = "Authorization", required = false) String token) {

        return null;
    }

//    @GetMapping("/day")
//    public ResponseEntity<?> getD_Day(@RequestHeader(value = "Authorization") String token) {
//
//        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
//        if (tokenCheckResponse != null) {
//            return tokenCheckResponse;
//        }
//        token=token.substring(7);
//        List<D_DayDTO> dDayList = toDoService.getD_Day(token);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("D_Day", dDayList);
//
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/day/save")
//    public ResponseEntity<?> saveD_Day(@RequestBody D_DayDTO dDayDTO, @RequestHeader(value = "Authorization") String token) {
//
//        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
//        if (tokenCheckResponse != null) {
//            return tokenCheckResponse;
//        }
//        token=token.substring(7);
//
//        Map<String, String> response = new HashMap<>();
//        toDoService.addD_Day(token,dDayDTO);
//        response.put("message", "D-DAY가 성공적으로 저장되었습니다.");
//
//        return ResponseEntity.ok(response);
//        }
//
//    @PostMapping("/day/delete")
//    public ResponseEntity<?> delete_D_Day(@RequestParam Long id, @RequestHeader(value = "Authorization") String token) {
//
//        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
//        if (tokenCheckResponse != null) {
//            return tokenCheckResponse;
//        }
//        Map<String, String> response = new HashMap<>();
//        toDoService.delete_D_Day(id);
//        response.put("message", "D-DAY가 성공적으로 삭제되었습니다.");
//
//        return ResponseEntity.ok(response);
//        }

}


