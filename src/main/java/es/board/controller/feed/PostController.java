package es.board.controller.feed;


import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.dto.feed.PostDTO;
import es.board.ex.TokenValidator;
import es.board.repository.entity.PostEntity;
import es.board.service.feed.PostService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class PostController {

    private final JwtTokenProvider provider;

    private final PostService postService;

    private final TokenValidator tokenValidator;

    @PostMapping("/post")
    public ResponseEntity<?> savePost(@RequestHeader(value = "Authorization", required = false) String token,
                                      @RequestPart("feed")
                                      PostDTO.Response response){
        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
        if (tokenCheckResponse == null) {
            return tokenCheckResponse;
        }
        String userId = provider.getUserId(token.substring(7));
        postService.savePost(userId,response);
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "message", "게시글이 정상적으로 저장되었습니다."
        ));
    }


    @GetMapping("/post/{id}")
    public ResponseEntity<?> getPost(@RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable int id){

        String currentUserId = checkToken(token);
        PostDTO.Request postDetail = postService.getPostDetail(currentUserId,id);
        return ResponseEntity.ok(Map.of("ok",postDetail));
    }


    @GetMapping("/posts/ids")
    public ResponseEntity<?> getPostIds(@RequestParam int page, @RequestParam int size) {
        Page<Integer> ids = postService.findIds(page, size);
        return ResponseEntity.ok(Map.of(
                "ids", ids));
    }


    @GetMapping("/posts")
    public ResponseEntity<?> getPosts(@RequestParam int page, @RequestParam int size) {
        Page<PostEntity> p = postService.getPosts(page, size);
        return ResponseEntity.ok(Map.of(
                "content", p.getContent(),
                "page", p.getNumber(),
                "size", p.getSize(),
                "totalPages", p.getTotalPages(),
                "totalElements", p.getTotalElements(),
                "last", p.isLast()
        ));
    }
    @DeleteMapping("/post/{id}")
    public ResponseEntity<?> deletePost(@PathVariable int id){
        postService.deletePost(id);
        return ResponseEntity.ok(Map.of("ok",true,
                                        "message","게시글이 삭제 되었습니다"
        ));
    }
    @PostMapping("/view/count")
    public ResponseEntity<?> increaseViewCount(@RequestBody Map<String, String> request,
                                               HttpServletResponse response,
                                               @CookieValue(value = "viewedFeeds", defaultValue = "") String viewedFeeds) {
        String id = (request.get("id"));
        if (!viewedFeeds.contains(id)) {
            postService.incrementViewCount(Integer.parseInt(id));
            String updatedFeeds = viewedFeeds.isEmpty() ? id : viewedFeeds + ";" + id;
            String encodedValue = URLEncoder.encode(updatedFeeds, StandardCharsets.UTF_8);
            Cookie cookie = new Cookie("viewedFeeds", encodedValue);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 40);
            response.addCookie(cookie);
        }
        return ResponseEntity.ok("조회수 증가 성공");
    }
    @GetMapping("/count")
    public ResponseEntity<?> getCountCommentAndReply(@RequestParam int page, @RequestParam int size) {
        Map<Integer, Long> countByCommentAndReply = postService.getCountByCommentAndReply(page, size);
        return ResponseEntity.ok(Map.of(
                "aggs", countByCommentAndReply));
    }

    @Nullable
    private String checkToken(String token) {
        String currentUserId = (token != null && token.startsWith("Bearer "))
                ? provider.getUserId(token.substring(7))
                : null;
        return currentUserId;
    }


}
