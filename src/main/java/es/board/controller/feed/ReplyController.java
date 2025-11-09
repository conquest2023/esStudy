package es.board.controller.feed;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.dto.feed.ReplyDTO;
import es.board.ex.TokenValidator;
import es.board.service.ReplyService;
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
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody ReplyDTO.Response response
            ){

        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
        if (tokenCheckResponse == null) {
            return tokenCheckResponse;
        }
        String userId = provider.getUserId(token.substring(7));

        replyService.saveReply(userId,response);


        return ResponseEntity.ok(Map.of(
                "ok", true,
                "message", "답글이 정상적으로 저장되었습니다."
        ));
    }

    @GetMapping("/replys")
    public ResponseEntity<?> getReplys(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam int postId
    ){
        String currentUserId = checkToken(token);

        List<ReplyDTO.Request> replies = replyService.getReplys(currentUserId,postId);
        return ResponseEntity.ok(Map.of("ok",replies));
    }
    @DeleteMapping("/reply/{id}")
    public ResponseEntity<?> deleteReply(@PathVariable int id){
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
