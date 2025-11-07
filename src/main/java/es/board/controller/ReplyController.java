package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.dto.feed.ReplyDTO;
import es.board.ex.TokenValidator;
import es.board.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            @RequestParam int postId
    ){
        List<ReplyDTO.Request> repiles = replyService.getReplys(postId);
        log.info(repiles.toString());
        return ResponseEntity.ok(Map.of("ok",repiles));
    }

}
