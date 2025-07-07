package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.req.CommentUpdate;
import es.board.controller.model.res.CommentDTO;
import es.board.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")

public class CommentController {

    private final CommentService commentService;

    private final JwtTokenProvider jwtTokenProvider;


    @PostMapping("/search/view/comment/id")
    @ResponseBody
    public ResponseEntity<?> saveCommentId(
            @RequestParam("feedUID") String id,
            @Valid @RequestBody CommentDTO.Response response,
            @RequestHeader(value = "Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                response.commentBasicSetting(id, jwtTokenProvider.getUsername(token), jwtTokenProvider.getUserId(token));
            }
        }
        commentService.saveComment(response);
        return ResponseEntity.ok(Map.of("redirectUrl", "/search/view/feed/id/" + id));
    }

    @PostMapping("/search/view/vote/comment/id")
    @ResponseBody
    public ResponseEntity<?> saveVoteCommentId(
            @RequestParam("feedUID") String id,
            @RequestBody CommentDTO.Response response,
            @RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                response.commentBasicSetting(id, jwtTokenProvider.getUsername(token), jwtTokenProvider.getUserId(token));
            }
        }
        commentService.saveComment(response);
        return ResponseEntity.ok(Map.of("redirectUrl", "/search/view/vote/detail?id=" + id));
    }

    @GetMapping("/commentEx")
    public void Test() {
        log.info(commentService.getMostCommentCount().toString());
    }


    @GetMapping("comment/update/get")
    @ResponseBody
    public ResponseEntity<?> updateComment(@RequestParam String commentUID) {

        Map<String, Object> response = Map.of(
                "id", commentService.getCommentOne(commentUID),
                "commentUID", commentUID,
                "redirectUrl", "basic/comment/EditComment");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/comment/update/save")
    @ResponseBody
    public ResponseEntity<?> editSaveComment(
            @RequestBody CommentUpdate commentUpdate) {
        Map<String, Object> response = Map.of(
                "commentUID", commentUpdate.getCommentUID(),
//
                "CommentUpdate", commentService.editComment(commentUpdate.getCommentUID(), commentUpdate),
                "redirectUrl", "/search/view/feed/id?id=" + commentUpdate.getFeedUID()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/view/comment/like")
    public String getLikeCount(Model model) {
        model.addAttribute("data", commentService.getLikeCount());
        return "basic/commentLike";
    }

    @GetMapping("/feeds/popular-by-comment")
    public ResponseEntity<?> getManyComment() {
        return ResponseEntity.ok(commentService.getMantComment());
    }


    @PostMapping("/search/view/comment/delete")
    @ResponseBody
    public ResponseEntity<?> CommentRemove(@RequestParam("id") String commentId,
                                           @RequestParam("feedUID") String feedId) {
        try {
            commentService.deleteComment(commentId);
            // 성공 응답 반환
            Map<String, String> response = new HashMap<>();
            response.put("redirectUrl", "/search/view/feed/id?id=" + feedId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("댓글 삭제 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/search/view/comment/increase-like/{commentUID}")
    public ResponseEntity<Map<String, Integer>> increaseLikeCount(@PathVariable String commentUID) {
        commentService.plusCommentLike(commentUID);
        Map<String, Integer> response = new HashMap<>();
        return ResponseEntity.ok(response);
    }

}
//    @GetMapping("/search/view/comment/time")
//    public String getRecentCommentList(Model model) {
//        model.addAttribute("data",commentService.getRecentComment());
//
//        return "basic/commentTime";
//    }
//
//    @GetMapping("/search/view/comment")
//    public String getCommentMainPage(@RequestParam String index, Model model) {
//        model.addAttribute("CommentDTO", new CommentDTO());
//
//        model.addAttribute("data",commentService.getComment());
//        return "basic/commentList";
//    }
//
//    @GetMapping("/search/view/comment/text")
//    public String getSearchCommentList(Model model, @RequestParam String text){
//        log.info(commentService.getSearchComment(text).toString());
//        model.addAttribute("data",commentService.getSearchComment(text));
//
//        return  "/basic/commentSearch";
//    }

//    @GetMapping("/search/view/comment/paging")
//    public String getPagingCommentList(Model model, @RequestParam(defaultValue = "1") int num)  {
//
////        model.addAttribute("currentPage",num);
////        model.addAttribute("totalPages", num+6
////        );
//////        model.addAttribute("data",commentService.getPagingComment(num,));
////
////        return  "basic/commentPaging";
//        return  null;
//    }



