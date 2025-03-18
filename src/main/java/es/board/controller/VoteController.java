package es.board.controller;


import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.req.VoteResponse;
import es.board.service.VoteService;
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
public class VoteController {

    private final VoteService voteService;


    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/save/vote")
    @ResponseBody
    public ResponseEntity<?> saveVote(@RequestBody VoteResponse vote, @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }
        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }
        voteService.saveVote(vote, jwtTokenProvider.getUsername(token), jwtTokenProvider.getUserId(token));
        Map<String, Object> response = new HashMap<>();
        return ResponseEntity.ok(response);
    }


    @PostMapping("/save/user/vote")
    @ResponseBody
    public ResponseEntity<?> saveUserVote(@RequestBody VoteResponse vote, @RequestHeader(value = "Authorization") String token) {
        log.info(vote.toString());
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }
        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }
        voteService.saveFeedVote(vote, jwtTokenProvider.getUsername(token), jwtTokenProvider.getUserId(token));
        Map<String, Object> response = new HashMap<>();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save/aggregation/vote")
    public ResponseEntity<?> saveAggregationVote(@RequestBody VoteResponse vote,
                                                 @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }
        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }
        voteService.saveAgreeVote(vote, jwtTokenProvider.getUsername(token), jwtTokenProvider.getUserId(token));
        Map<String, Object> response = new HashMap<>();
        response.put("message", "투표 완료");
        response.put("voteStatus", "completed");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/aggregation/vote/{id}")
    public ResponseEntity<?> getAggregationVote(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable Long id) {
        Map<String, Object> aggregation = voteService.getVoteAggregation(id);
        Map<String, Object> response = new HashMap<>();
        response.put("totalVotes", aggregation.get("totalVotes"));
        response.put("upvotes", aggregation.get("upvotes"));
        response.put("downvotes", aggregation.get("downvotes"));
        List<String> userIds = (List<String>) aggregation.get("userIds");
        if (!token.isEmpty()) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                boolean hasVoted = userIds.contains(jwtTokenProvider.getUserId(token));
                response.put("hasVoted", hasVoted);
                return ResponseEntity.ok(response);
            }
        }
        response.put("hasVoted", false);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/vote")
    public ResponseEntity<?> getVote() {

        return ResponseEntity.ok(voteService.getVoteContent());
    }

    @GetMapping("/get/vote/all")
    public ResponseEntity<?> getVoteAll() {
        return ResponseEntity.ok(Map.of(
                "vote", voteService.getVoteAll()));
    }

    @GetMapping("/get/user/vote")
    public ResponseEntity<?> getUserVote() {
        return ResponseEntity.ok((Map.of("data", voteService.getVoteUserAll())));
        }
    }
//    @GetMapping("/get/user/vote/details")
//    public ResponseEntity<?> getUserVoteDetail(@RequestParam String feedUID) {
//        return ResponseEntity.ok((Map.of("data", voteService.getVoteUserAll())));
//       }
//    }