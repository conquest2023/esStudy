package es.board.controller.feed;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.dto.feed.PostDTO;
import es.board.infrastructure.entity.feed.PostEntity;
import es.board.infrastructure.entity.user.User;
import es.board.infrastructure.projection.MyCommentProjection;
import es.board.infrastructure.projection.PostsAndCommentsProjection;
import es.board.service.AuthService;
import es.board.service.MyPageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MyPageController {


    private final AuthService userService;

    private final MyPageService myPageService;

    private final JwtTokenProvider jwtTokenProvider;


    @GetMapping("/user/information")
    public ResponseEntity<?> getSomeoneInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsername(token);
                User user = userService.findByUser(username);
                Map<String, Object> response = Map.of(
                        "username",username,
                        "userId",user.getUserId()
                );
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }
    @GetMapping("/mypage/point")
    public ResponseEntity<?> getSumMyPagePoint(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                int point = myPageService.getSumPointUser(jwtTokenProvider.getUserId(token));
                return ResponseEntity.ok(Map.of("point",point));

            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }

    @GetMapping("/mypage/post/paging")
    public ResponseEntity<?> getMyPagePosts(HttpServletRequest request,
                                             @RequestParam int page,
                                             @RequestParam int size) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                String userId = jwtTokenProvider.getUserId(token);
                Page<PostEntity> myPageFeedList = myPageService.getMyPageFeedList(page,size, userId);
                return ResponseEntity.ok(Map.of(
                        "totalCountPost",myPageFeedList.getTotalElements()
                        ,"totalPage",myPageFeedList.getTotalPages(),
                        "posts",myPageFeedList.get().collect(Collectors.toList()))
                );
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }
    @GetMapping("/mypage/comment/paging")
    public ResponseEntity<?> getMyPageComments(HttpServletRequest request,
                                             @RequestParam int page,
                                             @RequestParam int size) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String userId = jwtTokenProvider.getUserId(token);
            if (jwtTokenProvider.validateToken(token)) {
                Page<MyCommentProjection> myPageCommentsList = myPageService.getMyPageCommentList(page, size,userId);
                return ResponseEntity.ok(Map.of(
                        "totalCountComment",myPageCommentsList.getTotalElements()
                        ,"totalPage",myPageCommentsList.getTotalPages(),
                        "comments",myPageCommentsList.get().toList())
                );
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }

    @GetMapping("/mypage/post/comment/paging")
    public ResponseEntity<?> getMyPagePostsAndComments(HttpServletRequest request,
                                               @RequestParam int page,
                                               @RequestParam int size) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                Page<PostsAndCommentsProjection> postsCommentedByUser = myPageService.getPostsCommentedByUser(page, size, jwtTokenProvider.getUserId(token));
                return ResponseEntity.ok(Map.of(
                        "totalCountComment",postsCommentedByUser.getTotalElements()
                        ,"totalPage",postsCommentedByUser.getTotalPages(),
                        "all",postsCommentedByUser.get().toList())
                );
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }


}
