package es.board.controller;


import es.board.model.req.ReplyRequest;
import es.board.model.res.ReplyCreateResponse;
import es.board.repository.document.Reply;
import es.board.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ReplyViewController {

    private  final ReplyService replyService;

    @GetMapping("/search/view/reply")
    public List<ReplyRequest> getReplyAll(@RequestParam String id){

            return replyService.getPartialReply(id);
    }

    @PostMapping("/search/view/reply/save")
    public String postReply(@RequestBody ReplyCreateResponse replyCreateDTO) {
        // 로그로 받은 데이터를 확인합니다.
        log.info(replyCreateDTO.toString());
        // 답글 저장 서비스 호출
        replyService.saveReply(replyCreateDTO);

        // 리다이렉션 처리 (피드 페이지로 리다이렉션)
        return "redirect:/search/view/feed/id?id=" + replyCreateDTO.getFeedUID();
    }
}
