package es.board.controller;


import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.req.ScheduleRequest;
import es.board.service.CalenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public void saveTodo(@RequestHeader(value = "Authorization") String token, @RequestBody ScheduleRequest scheduleDTO) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                calenderService.saveSchedule(token,scheduleDTO);
            }
        }

    }
    @PostMapping("/save/repeat/schedule")
    public void saveRepeatCalendar(@RequestHeader(value = "Authorization") String token, @RequestBody ScheduleRequest scheduleDTO) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                calenderService.saveRepeatSchedule(token,scheduleDTO);
            }
        }

    }




    @GetMapping("/search/repeat/schedule")
    @ResponseBody
    public ResponseEntity<?> getRepeatSchedule(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }
        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }

        List<ScheduleRequest> scheduleDTOS = calenderService.getRepeatSchedule(token);
        Map<String, Object> response = new HashMap<>();
        response.put("repeatSchedules", scheduleDTOS);
        return ResponseEntity.ok(response);
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

        List<ScheduleRequest> scheduleDTOS = calenderService.getSchedule(token);
        Map<String, Object> response = new HashMap<>();
        response.put("todos", scheduleDTOS);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/elastic/schedule")
    @ResponseBody
    public ResponseEntity<?> searchSchedule(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "type", required = false) String searchType,
            @RequestParam(value = "sort", required = false) String sortType) {


        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }

        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }


        List<ScheduleRequest> scheduleDTOS = calenderService.searchSchedule(token, query, searchType, sortType);

        Map<String, Object> response = new HashMap<>();

        response.put("schedules", scheduleDTOS);

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

    @PostMapping("/delete/repeat/schedule")
    public ResponseEntity<?> deleteRepeatTodo(
            @RequestHeader(value = "Authorization") String token,
            @RequestBody Map<String, String> requestBody) {

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }
        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }
        log.info(requestBody.toString());
        String createdAtStr = requestBody.get("createdAt");
        String startDateTimeAtStr=requestBody.get("startDatetime");
        String endDateTimeAtStr=requestBody.get("endDatetime");



        LocalDateTime createdAt = LocalDateTime.parse(createdAtStr);

        LocalDateTime start = LocalDateTime.parse(startDateTimeAtStr);

        LocalDateTime end = LocalDateTime.parse(endDateTimeAtStr);


        calenderService.deleteRepeatSchedule(token, createdAt,start,end);

        return ResponseEntity.ok(Map.of("success", true));
    }
}
