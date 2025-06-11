package es.board.controller;


import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.mapper.FeedMapper;
import es.board.controller.model.req.ReplyRequest;
import es.board.controller.model.res.ReplyCreate;
import es.board.ex.TokenValidator;
import es.board.repository.document.Reply;
import es.board.service.ReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")

public class ReplyController {

    private final ReplyService replyService;

    private  final TokenValidator tokenValidator;

    private  final JwtTokenProvider jwtTokenProvider;


//    @GetMapping("/search/view/reply")
//    public List<ReplyRequest> getReplyAll(@RequestParam String id) {
//        return feedMapper.ReplyListToDTO((List<Reply>) replyService.getPartialReply(id).get("replyList"));
//    }

    @PostMapping("/search/view/reply/save")
    public ResponseEntity<?> postReply(@RequestHeader(value = "Authorization", required = false) String token,
                                            @Valid @RequestBody ReplyCreate response) {


        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
        if (tokenCheckResponse != null) {
            return tokenCheckResponse;
        }
        token=token.substring(7);
        response.replyBasicSetting(jwtTokenProvider.getUsername(token), jwtTokenProvider.getUserId(token));
        replyService.saveReply(response);
        return ResponseEntity.ok("/search/view/feed/id?id=" + response.getFeedUID());
    }



    @PostMapping("/search/view/vote/reply/save")
    public ResponseEntity<?> postVoteReply(@RequestHeader(value = "Authorization", required = false) String token,@RequestBody ReplyCreate response) {
        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
        if (tokenCheckResponse != null) {
            return tokenCheckResponse;
        }
        token=token.substring(7);
        response.replyBasicSetting(jwtTokenProvider.getUsername(token), jwtTokenProvider.getUserId(token));
        replyService.saveReply(response);
        return ResponseEntity.ok("/search/view/vote/detail?id=" + response.getFeedUID());
    }
}
