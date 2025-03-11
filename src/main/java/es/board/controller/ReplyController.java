package es.board.controller;


import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.mapper.FeedMapper;
import es.board.controller.model.req.ReplyRequest;
import es.board.controller.model.res.ReplyCreate;
import es.board.repository.document.Reply;
import es.board.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ReplyController {

    private final ReplyService replyService;

    private  final JwtTokenProvider jwtTokenProvider;

    private  final FeedMapper feedMapper;

//    @GetMapping("/search/view/reply")
//    public List<ReplyRequest> getReplyAll(@RequestParam String id) {
//        return feedMapper.ReplyListToDTO((List<Reply>) replyService.getPartialReply(id).get("replyList"));
//    }

    @PostMapping("/search/view/reply/save")
    public ResponseEntity<String> postReply(@RequestHeader(value = "Authorization", required = false) String token,@RequestBody ReplyCreate response) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                response.replyBasicSetting(jwtTokenProvider.getUserId(token));
            } else {
                response.replyAnonymousBasicSetting();
            }
        } else {
            response.replyAnonymousBasicSetting();
        }
        replyService.saveReply(response);
        return ResponseEntity.ok("/search/view/feed/id?id=" + response.getFeedUID());
    }
}
