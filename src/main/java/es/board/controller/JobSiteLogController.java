package es.board.controller;


import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.mapper.MainFunctionMapper;
import es.board.controller.model.res.InterviewAnswerDTO;
import es.board.controller.model.res.JobSiteLogDTO;
import es.board.service.JobSiteLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")

public class JobSiteLogController {

    private final JobSiteLogService jobSiteLogService;

    private  final  JwtTokenProvider jwtTokenProvider;

    private final MainFunctionMapper mapper;

    @PostMapping("/site/log")
    public ResponseEntity<?> saveSiteLog(@RequestBody JobSiteLogDTO dto, @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null) {
            jobSiteLogService.saveSiteLog(dto);
        }
        token = token.substring(7);
        if (jwtTokenProvider.validateToken(token)) {
           jobSiteLogService.saveSiteLog(mapper.toJobSiteDocument(dto, jwtTokenProvider.getUserId(token)));
        }
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "클릭 로그가 저장되었습니다."
        ));
    }

    @GetMapping("/search/site/log")
    public ResponseEntity<?> getSiteLog(
            @RequestHeader(value = "Authorization", required = false) String token) {

        List<JobSiteLogDTO> top5Sites = jobSiteLogService.getSiteLog();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "ranking", top5Sites
        ));
    }
}