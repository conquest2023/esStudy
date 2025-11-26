package es.board.controller.feed;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.dto.feed.ReplyDTO;
import es.board.ex.TokenValidator;
import es.board.domain.feed.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    private final JwtTokenProvider provider;


    private final TokenValidator tokenValidator;
    @PostMapping("/reply")
    public ResponseEntity<?> saveReply(
            @RequestAttribute("userId") String userId,
            @RequestBody ReplyDTO.Response response){
        replyService.saveReply(userId,response);


        return ResponseEntity.ok(Map.of(
                "ok", true,
                "message", "답글이 정상적으로 저장되었습니다."
        ));
    }

    @GetMapping("/replys")
    public ResponseEntity<?> getReplys(
            @RequestAttribute("userId") String userId,
            @RequestParam int postId
    ){
        List<ReplyDTO.Request> replies = replyService.getReplys(userId,postId);
        return ResponseEntity.ok(Map.of("ok",replies));
    }

    @PutMapping("reply/{id}")
    public ResponseEntity<?> updateReply(@PathVariable long id,
                                         @RequestAttribute("userId") String userId,
                                         @RequestBody ReplyDTO.Update update){
        ReplyDTO.Request reply = replyService.updateReply(id, update);
        return ResponseEntity.ok(Map.of("reply",reply));
    }

    @DeleteMapping("/reply/{id}")
    public ResponseEntity<?> deleteReply(@PathVariable int id,
                                          @RequestHeader(value = "Authorization", required = false) String token){
        checkToken(token);
        replyService.deleteReply(id);
        return ResponseEntity.ok(Map.of("ok",true,
                "message","게시글이 삭제 되었습니다"
        ));
    }

    @Nullable
    private String checkToken(String token) {
        String currentUserId = (token != null && token.startsWith("Bearer "))
                ? provider.getUserId(token.substring(7))
                : null;
        return currentUserId;
    }
}
