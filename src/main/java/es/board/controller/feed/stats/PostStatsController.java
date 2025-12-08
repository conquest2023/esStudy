package es.board.controller.feed.stats;

import es.board.controller.model.dto.feed.PostDTO;
import es.board.controller.model.dto.stats.PostStatsDTO;
import es.board.controller.model.mapper.entity.PostDomainMapper;
import es.board.infrastructure.entity.feed.PostEntity;
import es.board.infrastructure.jpa.projection.PostWithCommentCount;
import es.board.infrastructure.jpa.projection.PostWithLikeCount;
import es.board.infrastructure.jpa.projection.PostWithReplyCount;
import es.board.service.CommandQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(Map.of(
                "stats", postStats));
    }

    @GetMapping("/post/popular/stats/{day}")
    public ResponseEntity<?> getBestPopularPostStats(@PathVariable String day, @RequestParam int page, @RequestParam int size) {
        List<PostStatsDTO> postStats = commandQueryService.getBestPostStats(day,page, size);
        return ResponseEntity.ok(Map.of(
                "stats", postStats));
    }

    @GetMapping("/poll/stats")
    public ResponseEntity<?> getPollStats(@RequestParam int page, @RequestParam int size) {
        List<PostStatsDTO> postStats = commandQueryService.getPollStats(page, size);
        return ResponseEntity.ok(Map.of(
                "stats", postStats));
    }


    @GetMapping("/post/comment")
    public ResponseEntity<?> getPostWithCommentCount(@RequestParam int page, @RequestParam int size){
        Page<PostWithCommentCount> p = commandQueryService.getPostWithCommentCount(page, size);

        return ResponseEntity.ok(
                Map.of(
                        "page", p.getNumber(),
                        "size", p.getSize(),
                        "totalPages", p.getTotalPages(),
                        "totalElements", p.getTotalElements(),
                        "last", p.isLast(),
                        "content", p.getContent()));

    }

    @GetMapping("/post/reply")
    public ResponseEntity<?> getPostWithReplyCount(@RequestParam int page, @RequestParam int size){
        Page<PostWithReplyCount> p = commandQueryService.getPostWithReplyCount(page, size);
        return ResponseEntity.ok(
                Map.of(
                        "page", p.getNumber(),
                        "size", p.getSize(),
                        "totalPages", p.getTotalPages(),
                        "totalElements", p.getTotalElements(),
                        "last", p.isLast(),
                        "content", p.getContent()));

    }


    @GetMapping("/post/view")
    public ResponseEntity<?> getPostViewCount(@RequestParam int page, @RequestParam int size){
        Page<PostEntity> p = commandQueryService.getPostViewCount(page, size);
        List<PostDTO.Response> collect = PostDomainMapper.toResponse(p);
        return ResponseEntity.ok(
                Map.of(
                        "page", p.getNumber(),
                        "size", p.getSize(),
                        "totalPages", p.getTotalPages(),
                        "totalElements", p.getTotalElements(),
                        "last", p.isLast(),
                        "content", collect));
    }

    @GetMapping("/post/like")
    public ResponseEntity<?> getPostLikeCount(@RequestParam int page, @RequestParam int size){
        Page<PostWithLikeCount> p = commandQueryService.findByPostWithLikeCount(page, size);
        return ResponseEntity.ok(
                Map.of(
                        "page", p.getNumber(),
                        "size", p.getSize(),
                        "totalPages", p.getTotalPages(),
                        "totalElements", p.getTotalElements(),
                        "last", p.isLast(),
                        "content", p.getContent()));
    }

    @PostMapping("/post/stats/by-ids")
    public ResponseEntity<?> getPostStatsByIds(@RequestBody List<Integer> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return ResponseEntity.ok(Map.of("stats", List.of()));
        }
        List<PostStatsDTO> stats = commandQueryService.getPostStatsByIds(postIds);
        return ResponseEntity.ok(Map.of("stats", stats));
    }

}
