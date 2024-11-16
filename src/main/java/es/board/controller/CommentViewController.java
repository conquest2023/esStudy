package es.board.controller;

import es.board.model.req.ReqCommentDTO;
import es.board.model.req.ReqSearchCommentDTO;
import es.board.model.req.UpdateCommentDTO;
import es.board.model.res.CommentSaveDTO;
import es.board.model.res.FeedSaveDTO;
import es.board.repository.document.Comment;
import es.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@Controller
@Slf4j
@RequiredArgsConstructor
public class CommentViewController {

    private final CommentService commentService;

    @GetMapping("/search/view/comment/{id}")
    public String searchGetId(Model model, @PathVariable String id) throws IOException{
        model.addAttribute("data",commentService.SearchId(id));
        return "basic/commentAll";
    }
    @GetMapping("/search/view/comment/time")
    public String searchNewFeedDSEC(Model model) throws IOException {
        model.addAttribute("data",commentService.searchTimeDESC());

        return "basic/commentTime";

    }
    @GetMapping("/search/view/comment/text")
    public String searchText(Model model,@RequestParam String text) throws IOException {
        log.info(commentService.SearchTextEx(text).toString());
        model.addAttribute("data",commentService.SearchTextEx(text));

        return  "/basic/commentSearch";
    }
    @GetMapping("/search/view/comment/like")
    public String LikeDESC(Model model) throws IOException {
          model.addAttribute("data",commentService.LikeDESCTo());
          return  "basic/commentLike";
    }
    @GetMapping("/search/view/comment/paging")
    public String PagingSearch(Model model,@RequestParam(defaultValue = "1") int num) throws IOException {

        model.addAttribute("currentPage",num);
        model.addAttribute("totalPages", num+6
        );
        model.addAttribute("data",commentService.PagingSearchIndex(num));

        return  "basic/commentPaging";
    }

    // 문서 색인
    @PostMapping("/search/view/comment/save")
    public  String CommentSave(@ModelAttribute CommentSaveDTO commentSaveDTO) throws IOException {
        commentService.indexComment(commentSaveDTO);
        return "redirect:/";  // 저장 후 메인 페이지로 리다이렉트;
    }

    @GetMapping("/search/view/comment/word/{keyword}")
    public String CommentSearch(Model model, @PathVariable("keyword") String keyword)
    {
        model.addAttribute("data",commentService.SearchComment(keyword));

        return   "basic/commentAllasdasd";
    }

    @GetMapping("/search/view/comment")
    public  String Comment(Model model,@RequestParam String index) throws IOException {
        model.addAttribute("CommentSaveDTO", new CommentSaveDTO());
        model.addAttribute("data",commentService.searchIndex(index));

        return  "basic/commentList";
    }

    @GetMapping("/comment/view/score/{score}")
    public List<ReqCommentDTO>  CommentScore(@PathVariable("score") String score){

        return commentService.CommentScore(score);
    }

    @GetMapping("/search/view/comment/commentAll")
    public String CommentGet(Model model){
       // model.addAttribute("CommentSaveDTO", new CommentSaveDTO());
        model.addAttribute("data",commentService.CommentBring());

        return "basic/commentAll";
    }
    @PutMapping("/search/view/update/{CommentUID}")
    public String CommentUpdate(Model model,@PathVariable String CommentUID,@RequestBody UpdateCommentDTO updateCommentDTO) throws IOException {

        model.addAttribute("data",commentService.EditCommentEx(CommentUID,updateCommentDTO));

        return  "basic/commentAll";

    }




//    @DeleteMapping("/delete/comment/{id}")
//    public  void CommentRemove(String commentUid){
//
//        commentService.CommentRemoveId(commentUid);
//    }
}
