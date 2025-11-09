package es.board.controller.feed;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.dto.feed.CommentDTO;
import es.board.ex.TokenValidator;
import es.board.service.CommentService;
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

    private final TokenValidator tokenValidator;
    @PostMapping("/comment")
    public ResponseEntity<?> saveComment(
            @RequestHeader(value = "Authorization") String token,
            @RequestBody CommentDTO.Response response){
        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
        if (tokenCheckResponse == null) {
            return tokenCheckResponse;
        }
        String userId = provider.getUserId(token.substring(7));
        commentService.saveComment(userId,response);
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "message", "댓글이 정상적으로 저장되었습니다."
        ));
    }


    @GetMapping("/comment/{id}")
    public ResponseEntity<?> getComment(@PathVariable long id){

        return null;
    }


    @GetMapping("/comments")
    public ResponseEntity<?> getComments(
            @RequestHeader(value = "Authorization",required = false) String token,
            @RequestParam int postId){

        String currentUserId = checkToken(token);

        List<CommentDTO.Request> comments = commentService.getComments(currentUserId,postId);
        return ResponseEntity.ok(Map.of("ok",comments));
    }

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable int id){
        log.info("삭제완료={}",id);
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
