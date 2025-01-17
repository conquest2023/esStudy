package es.board.controller;

import es.board.controller.model.req.CommentUpdate;
import es.board.controller.model.res.CommentCreateResponse;
import es.board.service.CommentService;
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
public class CommentController {

    private final CommentService commentService;


//    @PostMapping("/search/view/feed/id")
////    @ResponseBody
//    public String saveCommentId(@RequestParam String id,
//                                @ModelAttribute CommentCreateResponse commentSaveDTO,
//                                Model model)  {
//        commentSaveDTO.commentBasicSetting(id);
//        commentService.indexComment(commentSaveDTO);
//        model.addAttribute("push",commentSaveDTO);
//        return "redirect:/search/view/feed/id?id=" + id;
//    }
    @PostMapping("/search/view/comment/id")
    @ResponseBody
    public ResponseEntity<?> saveCommentId(
            @RequestParam("feedUID") String id, // URL 쿼리 매개변수 feedUID
            @RequestBody CommentCreateResponse commentSaveDTO) {

        log.info(commentSaveDTO.toString());
        // 댓글 기본 설정 및 저장
        commentSaveDTO.commentBasicSetting(id);
        commentService.indexComment(commentSaveDTO);
        // 리다이렉트 URL 반환
        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", "/search/view/feed/id?id=" + id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("comment/update/get")
    @ResponseBody
    public ResponseEntity<?> updateComment(@RequestParam String commentUID) {

         Map<String, Object> response = Map.of(
                "id", commentService.getCommentOne(commentUID),
                "commentUID", commentUID,
                "redirectUrl", "basic/comment/EditComment");
            return ResponseEntity.ok(response);}
    @PostMapping("/comment/update/save")
    @ResponseBody
    public ResponseEntity<?> editSaveComment(
                                  @RequestBody CommentUpdate commentUpdate) {
        log.info(commentUpdate.toString());
        Map<String, Object> response = Map.of(
                "commentUID", commentUpdate.getCommentUID(),
//                "feedUID", commentUpdate.getFeedUID(),
                "CommentUpdate",   commentService.editComment(commentUpdate.getCommentUID(), commentUpdate),
                "redirectUrl", "/search/view/feed/id?id=" + commentUpdate.getFeedUID()
        );
        return ResponseEntity.ok(response);
    }
    @GetMapping("/search/view/comment/like")
    public String getLikeCount(Model model) {
        model.addAttribute("data", commentService.getLikeCount());
        return "basic/commentLike";
    }

    @PostMapping("/search/view/comment/delete")
    @ResponseBody // JSON 응답을 위해 추가
    public ResponseEntity<?> CommentRemove(@RequestParam("id") String commentId,
                                           @RequestParam("feedUID") String feedId) {
        try {
            commentService.deleteComment(commentId); // 댓글 삭제 로직 수행
            // 성공 응답 반환
            Map<String, String> response = new HashMap<>();
            response.put("redirectUrl", "/search/view/feed/id?id=" + feedId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 에러 응답 반환
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
//        model.addAttribute("CommentCreateResponse", new CommentCreateResponse());
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



