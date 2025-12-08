package es.board.controller.feed;


import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.dto.feed.PostDTO;
import es.board.controller.model.dto.poll.PollDto;
import es.board.controller.model.dto.poll.PollVoteDTO;
import es.board.infrastructure.entity.feed.PostEntity;
import es.board.domain.poll.PollService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class PollController {

    private final JwtTokenProvider provider;

    private final PollService pollService;

    @PostMapping("/poll")
    public ResponseEntity<?> savePoll(
            @RequestHeader(value = "Authorization") String token,
            @RequestBody PollDto.Request req){
        String userId = checkToken(token);
        String username = provider.getUsername(token.substring(7));
        pollService.createPoll(username,userId,req);
        return ResponseEntity.ok(Map.of(200,"투표가 생성되었습니다"));
    }

    @GetMapping("/polls")
    public ResponseEntity<?> getPostIds(@RequestParam int page, @RequestParam int size) {

        Page<PostEntity> poll = pollService.getPollList(page, size);
        List<PostDTO.Response> collect = poll.stream()
                .map(o -> new PostDTO.Response(o.getId(),
                        o.getUsername(),
                        o.getTitle(),
                        o.getDescription(),
                        o.getCategory(),
                        o.getViewCount(),
                        o.getCreatedAt(),
                        o.getModifiedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(
                Map.of(
                        "page", poll.getNumber(),
                        "size", poll.getSize(),
                        "totalPages", poll.getTotalPages(),
                        "totalElements", poll.getTotalElements(),
                        "last", poll.isLast(),
                        "content",collect));
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
    @PostMapping("/votes")
    public ResponseEntity<?> saveVotes(@RequestHeader(value = "Authorization") String token,
                                       @RequestBody PollVoteDTO.MultiRequest req){

        String userId = checkToken(token);
        pollService.voteAll(userId,req);
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
