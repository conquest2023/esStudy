//package es.board.controller;
//
//import es.board.config.jwt.JwtTokenProvider;
//import es.board.controller.model.req.ScheduleDTO;
//import es.board.service.TeamCalenderService;
//import es.board.service.TeamService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequiredArgsConstructor
//@Slf4j
//@RequestMapping("/team/calendar") // ✅ 모든 URL에 /team/calendar 추가
//public class TeamCalendarController {
//
//
//    private final JwtTokenProvider jwtTokenProvider;
//    private final TeamCalenderService teamCalenderService;
//    private final TeamService teamService; // ✅ 팀원 여부 확인을 위한 서비스
//
//    /**
//     * ✅ 팀 일정 추가 (Create)
//     */
//    @PostMapping("/save")
//    public ResponseEntity<?> saveSchedule(
//            @RequestHeader(value = "Authorization") String token,
//            @RequestParam Long teamId,
//            @RequestBody ScheduleDTO scheduleDTO) {
//
//        if (token == null || !token.startsWith("Bearer ")) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
//        }
//        token = token.substring(7);
//        if (!jwtTokenProvider.validateToken(token)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
//        }
//
//        String userId = jwtTokenProvider.getUserId(token);
//
//        // ✅ 유저가 해당 팀의 멤버인지 확인
//        if (!teamService.isTeamMember(teamId, userId)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "해당 팀의 멤버가 아닙니다."));
//        }
//
//        teamCalenderService.saveSchedule(teamId, userId, scheduleDTO);
//        return ResponseEntity.ok(Map.of("message", "일정이 저장되었습니다."));
//    }
//
//    /**
//     * ✅ 팀 일정 조회 (Read)
//     */
//    @GetMapping("/search")
//    public ResponseEntity<?> getTeamSchedules(
//            @RequestHeader(value = "Authorization") String token,
//            @RequestParam Long teamId) {
//
//        if (token == null || !token.startsWith("Bearer ")) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
//        }
//        token = token.substring(7);
//        if (!jwtTokenProvider.validateToken(token)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
//        }
//
//        String userId = jwtTokenProvider.getUserId(token);
//
//
//        // ✅ 유저가 팀원인지 확인
//        if (!teamService.isTeamMember(teamId, userId)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "해당 팀의 멤버가 아닙니다."));
//        }
//
//        List<ScheduleDTO> schedules = teamCalenderService.getSchedulesByTeam(teamId);
//        return ResponseEntity.ok(Map.of("schedules", schedules));
//    }
//
//    /**
//     * ✅ 팀 일정 삭제 (Delete)
//     */
//    @PostMapping("/delete/{scheduleId}")
//    public ResponseEntity<?> deleteSchedule(
//            @RequestHeader(value = "Authorization") String token,
//            @PathVariable Long scheduleId,
//            @RequestParam Long teamId) {
//
//        if (token == null || !token.startsWith("Bearer ")) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
//        }
//        token = token.substring(7);
//        if (!jwtTokenProvider.validateToken(token)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
//        }
//
//        String userId = jwtTokenProvider.getUserId(token);
//
//
//        // ✅ 유저가 팀원인지 확인
//        if (!teamService.isTeamMember(teamId, userId)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "해당 팀의 멤버가 아닙니다."));
//        }
//
//        teamCalenderService.deleteSchedule(teamId, scheduleId);
//        return ResponseEntity.ok(Map.of("message", "일정이 삭제되었습니다."));
//    }
//
//    /**
//     * ✅ 일정 검색 (ElasticSearch 연동)
//     */
//    @GetMapping("/search/elastic")
//    public ResponseEntity<?> searchTeamSchedule(
//            @RequestHeader(value = "Authorization") String token,
//            @RequestParam Long teamId,
//            @RequestParam(value = "query", required = false) String query,
//            @RequestParam(value = "type", required = false) String searchType,
//            @RequestParam(value = "sort", required = false) String sortType) {
//
//        if (token == null || !token.startsWith("Bearer ")) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
//        }
//        token = token.substring(7);
//        if (!jwtTokenProvider.validateToken(token)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
//        }
//
//        String userId = jwtTokenProvider.getUserId(token);
//
//
//        // ✅ 유저가 팀원인지 확인
//        if (!teamService.isTeamMember(teamId, userId)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "해당 팀의 멤버가 아닙니다."));
//        }
//
//        List<ScheduleDTO> schedules = teamCalenderService.searchSchedule(teamId, query, searchType, sortType);
//        return ResponseEntity.ok(Map.of("schedules", schedules));
//    }
//}
//
