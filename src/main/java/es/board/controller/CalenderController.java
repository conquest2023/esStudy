package es.board.controller;


import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.req.ScheduleDTO;
import es.board.controller.model.req.TodoRequest;
import es.board.controller.model.res.TodoResponse;
import es.board.service.CalenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CalenderController {

    private  final JwtTokenProvider jwtTokenProvider;

    private  final CalenderService calenderService;

    @PostMapping("/save/schedule")
    public void saveTodo(@RequestHeader(value = "Authorization") String token, @RequestBody ScheduleDTO scheduleDTO) {

        log.info(scheduleDTO.toString());
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                calenderService.saveSchedule(token,scheduleDTO);
            }
        }

    }
    @GetMapping("/search/schedule")
    @ResponseBody
    public ResponseEntity<?> getTodoById(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }
        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }

        List<ScheduleDTO> scheduleDTOS = calenderService.getSchedule(token);
        Map<String, Object> response = new HashMap<>();
        response.put("todos", scheduleDTOS);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/schedule/delete/{id}")
    public void deleteTodo(@PathVariable Long id,@RequestHeader(value = "Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                calenderService.deleteSchedule(id,token);
            }
        }
    }
}
