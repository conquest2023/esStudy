package es.board.controller;


import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.dto.poll.PollDto;
import es.board.controller.model.dto.poll.PollVoteDTO;
import es.board.service.poll.PollService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PollController {

    private final JwtTokenProvider provider;

    private final PollService pollService;

    @PostMapping("/poll")
    public ResponseEntity<?> savePoll(
            @RequestHeader(value = "Authorization") String token,
            @RequestBody PollDto.Request res){
        String userId = checkToken(token);
        pollService.createPoll(userId,res);
        return ResponseEntity.ok(Map.of(200,"투표가 생성되었습니다"));
    }


    @GetMapping("/poll/check/{pollId}")
    public ResponseEntity<?> getPoll(
            @RequestHeader(value = "Authorization",required = false) String token,
            @PathVariable long pollId){
        String userId = checkToken(token);
        boolean checkVoteUser = pollService.isCheckVoteUser(pollId, userId);
        return ResponseEntity.ok(Map.of("check",checkVoteUser));
    }

    @PostMapping("/vote")
    public ResponseEntity<?> saveVote(
            @RequestHeader(value = "Authorization") String token,
            @RequestBody PollVoteDTO.Request req){
        String userId = checkToken(token);
        pollService.vote(userId,req);
        return ResponseEntity.ok(Map.of(200,"투표가 작성되었습니다"));
    }

    @Nullable
    private String checkToken(String token) {
        String currentUserId = (token != null && token.startsWith("Bearer "))
                ? provider.getUserId(token.substring(7))
                : null;
        return currentUserId;
    }

}
