package es.board.controller;

import es.board.model.req.CommentUpdate;
import es.board.model.req.FeedUpdate;
import es.board.model.res.CommentCreateResponse;
import es.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Controller
@Slf4j
@RequiredArgsConstructor
public class CommentViewController {

    private final CommentService commentService;

//    @GetMapping("/search/view/comment/{id}")
//    public String getCommentId(Model model, @PathVariable String id) throws IOException{
//        model.addAttribute("data",commentService.getCommentId(id));
//        return "basic/commentAll";
//    }
@PostMapping("/search/view/comment/increase-like/{commentUID}")
public ResponseEntity<Map<String, Integer>> increaseLikeCount(@PathVariable String commentUID) {
    log.info(commentUID);
    commentService.plusCommentLike(commentUID);
    Map<String, Integer> response = new HashMap<>();
    return ResponseEntity.ok(response);
}
    @GetMapping("/search/view/comment/time")
    public String getRecentCommentList(Model model) {
        model.addAttribute("data",commentService.getRecentComment());

        return "basic/commentTime";
    }

    @GetMapping("/search/view/comment")
    public String getCommentMainPage(@RequestParam String index, Model model) {
        model.addAttribute("CommentCreateResponse", new CommentCreateResponse());

        model.addAttribute("data",commentService.getComment());
        return "basic/commentList";
    }



    @GetMapping("/search/view/comment/text")
    public String getSearchCommentList(Model model, @RequestParam String text){
        log.info(commentService.getSearchComment(text).toString());
        model.addAttribute("data",commentService.getSearchComment(text));

        return  "/basic/commentSearch";
    }
    @GetMapping("/search/view/comment/like")
    public String getLikeCount(Model model) {
          model.addAttribute("data",commentService.getLikeCount());
          return  "basic/commentLike";
    }
//    @GetMapping("/search/view/comment/desc")
//    public  String  getMostCommentDESC(Model model) throws  IOException{
//    }

    @GetMapping("/search/view/comment/paging")
    public String getPagingCommentList(Model model, @RequestParam(defaultValue = "1") int num)  {

//        model.addAttribute("currentPage",num);
//        model.addAttribute("totalPages", num+6
//        );
////        model.addAttribute("data",commentService.getPagingComment(num,));
//
//        return  "basic/commentPaging";
        return  null;
    }

    @GetMapping("/search/view/comment/post")
    public  String commentPost() {
//        commentService.indexComment(commentSaveDTO);
        return  "basic/comment/PostComment";
    }

    // 문서 색인
//    @PostMapping("/search/view/comment/save")
//    public  String commentSave(Model model, @ModelAttribute CommentCreateResponse commentSaveDTO) throws IOException {
//        log.info(commentSaveDTO.toString());
//        model.addAttribute("data",commentService.indexComment(commentSaveDTO));
//        return "basic/feed/FeedDetails";
//    }



//    @GetMapping("/search/view/comment/commentAll")
//    public String getComment(Model model) throws IOException {
//        model.addAttribute("data",commentService.getComment());
//        return "basic/commentAll";
//    }
    @GetMapping("/search/view/comment/update")
    public String updateComment(Model model, @RequestParam String id,  @ModelAttribute CommentUpdate commentUpdate)  {
        model.addAttribute("id", id);
        model.addAttribute("CommentUpdate", commentUpdate);
        return  "basic/comment/EditComment";
    }
    @PostMapping("/search/view/comment/update/save")
    public String editSaveComment(Model model,@ModelAttribute CommentUpdate commentUpdate)  {
        model.addAttribute("CommentUpdate", commentUpdate);
        log.info(commentUpdate.toString());
        commentService.editComment(commentUpdate.getFeedUID(),commentUpdate);
        return  "redirect:/search/view/feed/id?id=" + commentUpdate.getFeedUID();
    }


//    @PostMapping("/search/view/feed/delete")
//    public  String deleteFeed(@RequestParam String id) throws IOException {
//        log.info("hello");
//        feedService.deleteFeed(id);
//        return "redirect:/search/view/feed?index=board";
//    }

    @PostMapping("/search/view/comment/delete")
    public  String CommentRemove(@RequestParam  String id)  {
        commentService.deleteComment(id);
        return "redirect:/search/view/feed/id?id=" + id;

    }
}
