package es.board.controller;

import es.board.model.req.CommentUpdate;
import es.board.model.res.CommentCreateResponse;
import es.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@Controller
@Slf4j
@RequiredArgsConstructor
public class CommentViewController {

    private final CommentService commentService;

    @GetMapping("/search/view/comment/{id}")
    public String getCommentId(Model model, @PathVariable String id) throws IOException{
        model.addAttribute("data",commentService.getCommentId(id));
        return "basic/commentAll";
    }
    @GetMapping("/search/view/comment/time")
    public String getRecentCommentList(Model model) throws IOException {
        model.addAttribute("data",commentService.getRecentComment());

        return "basic/commentTime";
    }

    @GetMapping("/search/view/comment")
    public String getCommentMainPage(@RequestParam String index, Model model) throws IOException {
        model.addAttribute("CommentCreateResponse", new CommentCreateResponse());

        model.addAttribute("data",commentService.getComment());
        return "basic/commentList";
    }


    @GetMapping("/search/view/comment/text")
    public String getSearchCommentList(Model model, @RequestParam String text) throws IOException {
        log.info(commentService.getSearchComment(text).toString());
        model.addAttribute("data",commentService.getSearchComment(text));

        return  "/basic/commentSearch";
    }
    @GetMapping("/search/view/comment/like")
    public String getLikeCount(Model model) throws IOException {
          model.addAttribute("data",commentService.getLikeCount());
          return  "basic/commentLike";
    }
    @GetMapping("/search/view/comment/paging")
    public String getPagingCommentList(Model model, @RequestParam(defaultValue = "1") int num) throws IOException {

        model.addAttribute("currentPage",num);
        model.addAttribute("totalPages", num+6
        );
        model.addAttribute("data",commentService.getPagingComment(num));

        return  "basic/commentPaging";
    }

    // 문서 색인
    @PostMapping("/search/view/comment/save")
    public  String commentSave(@ModelAttribute CommentCreateResponse commentSaveDTO) throws IOException {
        commentService.indexComment(commentSaveDTO);
        return "redirect:/";  // 저장 후 메인 페이지로 리다이렉트;
    }



    @GetMapping("/search/view/comment/commentAll")
    public String getComment(Model model) throws IOException {
        model.addAttribute("data",commentService.getComment());

        return "basic/commentAll";
    }
    @PutMapping("/search/view/update/{CommentUID}")
    public String updateComment(Model model, @PathVariable String CommentUID, @RequestBody CommentUpdate updateCommentDTO) throws IOException {

        model.addAttribute("data",commentService.editComment(CommentUID,updateCommentDTO));

        return  "basic/commentAll";

    }




//    @DeleteMapping("/delete/comment/{id}")
//    public  void CommentRemove(String commentUid){
//
//        commentService.CommentRemoveId(commentUid);
//    }
}
