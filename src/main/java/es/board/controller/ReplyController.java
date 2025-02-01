package es.board.controller;


import es.board.controller.model.req.ReplyRequest;
import es.board.controller.model.res.ReplyCreate;
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

    @GetMapping("/search/view/reply")
    public List<ReplyRequest> getReplyAll(@RequestParam String id) {
        return replyService.getPartialReply(id);
    }

    @PostMapping("/search/view/reply/save")
    public ResponseEntity<String> postReply(@RequestBody ReplyCreate replyCreateDTO) {
        replyService.saveReply(replyCreateDTO);

        return ResponseEntity.ok("/search/view/feed/id?id=" + replyCreateDTO.getFeedUID());
    }
}
