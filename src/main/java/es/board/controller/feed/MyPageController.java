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
import org.springframework.web.bind.annotation.*;

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
                        "username", username,
                        "userId", user.getUserId()
                );
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }

    @GetMapping("/mypage/point")
    public ResponseEntity<?> getSumMyPagePoint(HttpServletRequest request, @RequestAttribute("userId") String userId) {
        int point = myPageService.getSumPointUser(userId);
        return ResponseEntity.ok(Map.of("point", point));
    }

    @GetMapping("/mypage/post/paging")
    public ResponseEntity<?> getMyPagePosts(HttpServletRequest request,
                                            @RequestAttribute("userId") String userId,
                                             @RequestParam int page,
                                             @RequestParam int size) {

                Page<PostEntity> myPageFeedList = myPageService.getMyPageFeedList(page,size, userId);
                return ResponseEntity.ok(Map.of(
                        "totalCountPost",myPageFeedList.getTotalElements()
                        ,"totalPage",myPageFeedList.getTotalPages(),
                        "posts",myPageFeedList.get().collect(Collectors.toList()))
                );
            }

    @GetMapping("/mypage/comment/paging")
    public ResponseEntity<?> getMyPageComments(HttpServletRequest request,
                                               @RequestAttribute("userId") String userId,
                                             @RequestParam int page,
                                             @RequestParam int size) {

                Page<MyCommentProjection> myPageCommentsList = myPageService.getMyPageCommentList(page, size,userId);
                return ResponseEntity.ok(Map.of(
                        "totalCountComment",myPageCommentsList.getTotalElements()
                        ,"totalPage",myPageCommentsList.getTotalPages(),
                        "comments",myPageCommentsList.get().toList())
                );
            }

    @GetMapping("/mypage/post/comment/paging")
    public ResponseEntity<?> getMyPagePostsAndComments(HttpServletRequest request,
                                                       @RequestAttribute("userId") String userId,
                                               @RequestParam int page,
                                               @RequestParam int size) {
                Page<PostsAndCommentsProjection> postsCommentedByUser = myPageService.getPostsCommentedByUser(page, size,userId);
                return ResponseEntity.ok(Map.of(
                        "totalCountComment",postsCommentedByUser.getTotalElements()
                        ,"totalPage",postsCommentedByUser.getTotalPages(),
                        "all",postsCommentedByUser.get().toList())
                );
            }
}
