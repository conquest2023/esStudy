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
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class VoteController {

    private final VoteService voteService;


    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/save/vote")
    @ResponseBody
    public ResponseEntity<?> saveAgreeVote(@RequestBody VoteResponse vote, @RequestHeader(value = "Authorization", required = false) String token) {
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

    @GetMapping("/get/vote")
    public ResponseEntity<?> getVote() {
        return ResponseEntity.ok(voteService.getVoteContent());
    }
    @GetMapping("/get/vote/all")
    public ResponseEntity<?> getVoteAll() {
        return ResponseEntity.ok(Map.of(
                "vote", voteService.getVoteAll()));
        }
    }

