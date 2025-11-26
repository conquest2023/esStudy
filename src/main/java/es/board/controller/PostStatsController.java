package es.board.controller;

import es.board.controller.model.dto.stats.PostStatsDTO;
import es.board.service.CommandQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostStatsController {

    private final CommandQueryService commandQueryService;


    @GetMapping("/post/stats")
    public ResponseEntity<?> getPostStats(@RequestParam int page, @RequestParam int size) {
        List<PostStatsDTO> postStats = commandQueryService.getPostStats(page, size);
        log.info(postStats.toString());
        return ResponseEntity.ok(Map.of(
                "stats", postStats));
    }

    @GetMapping("/post/popular/stats")
    public ResponseEntity<?> getBestPopularPostStats(@RequestParam int page, @RequestParam int size) {
        List<PostStatsDTO> postStats = commandQueryService.getBestWeekPostStats(page, size);
        log.info(postStats.toString());
        return ResponseEntity.ok(Map.of(
                "stats", postStats));
    }

    @GetMapping("/poll/stats")
    public ResponseEntity<?> getPollStats(@RequestParam int page, @RequestParam int size) {
        List<PostStatsDTO> postStats = commandQueryService.getPollStats(page, size);
        log.info(postStats.toString());
        return ResponseEntity.ok(Map.of(
                "stats", postStats));
    }




}
