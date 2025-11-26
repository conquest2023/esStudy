package es.board.controller.feed;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.dto.feed.CommentDTO;
import es.board.ex.TokenValidator;
import es.board.domain.feed.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {


    private final CommentService commentService;

    private final JwtTokenProvider provider;

    @PostMapping("/comment")
    public ResponseEntity<?> saveComment(
            @RequestAttribute("userId") String userId,
            @RequestBody CommentDTO.Response response){
        commentService.saveComment(userId,response);
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "message", "댓글이 정상적으로 저장되었습니다."
        ));
    }


    @PutMapping("/comment/{id}")
    public ResponseEntity<?> updateComment(@PathVariable long id,
                                           @RequestAttribute("userId") String userId,
                                           @RequestBody CommentDTO.Update update){
        CommentDTO.Request request = commentService.updateComment(id, update);
        return ResponseEntity.ok(Map.of("comment",request));
    }


    @GetMapping("/comments")
    public ResponseEntity<?> getComments(
            @RequestAttribute("userId") String userId,
            @RequestParam int postId){

        List<CommentDTO.Request> comments = commentService.getComments(userId,postId);
        return ResponseEntity.ok(Map.of("ok",comments));
    }

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable int id){
        commentService.deleteComment(id);
        return ResponseEntity.ok(Map.of("ok",true,
                "message","게시글이 삭제 되었습니다"
        ));
    }

    @Nullable
    private String checkToken(String token) {
        String currentUserId = (token != null && token.startsWith("Bearer "))
                ? provider.getUserId(token.substring(7))
                : null;
        return currentUserId;
    }
}
