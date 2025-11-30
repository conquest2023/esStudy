package es.board.controller;


import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.dto.feed.PostDTO;
import es.board.controller.model.mapper.PostDomainMapper;
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

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SomeoneProfileController {

    private final JwtTokenProvider jwtTokenProvider;


    private final AuthService userService;

    private final MyPageService myPageService;

    @GetMapping("/someone/point")
    public ResponseEntity<?> getSumMyPagePoint(HttpServletRequest request,
                                               @RequestParam String username) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                User user = userService.findByUser(username);
                int point = myPageService.getSumPointUser(user.getUserId());
                return ResponseEntity.ok(Map.of("point",point));

            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }
    @GetMapping("/someone")
    public ResponseEntity<?> getSomeone(HttpServletRequest request,
                                        @RequestParam String username,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                User user = userService.findByUser(username);
                Page<PostEntity> myPageFeedList = myPageService.getMyPageFeedList(page, size,user.getUserId());
                List<PostDTO.Response> collect = PostDomainMapper.toResponse(myPageFeedList);
                return ResponseEntity.ok(
                        Map.of(
                                "page", myPageFeedList.getNumber(),
                                "size", myPageFeedList.getSize(),
                                "totalPages", myPageFeedList.getTotalPages(),
                                "totalElements", myPageFeedList.getTotalElements(),
                                "last", myPageFeedList.isLast(),
                                "content",collect));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }

    @GetMapping("/someone/comment/paging")
    public ResponseEntity<?> getMyPageComments(HttpServletRequest request,
                                               @RequestParam String username,
                                               @RequestParam int page,
                                               @RequestParam int size) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                User user = userService.findByUser(username);
                Page<MyCommentProjection> myPageCommentsList = myPageService.getMyPageCommentList(page, size,user.getUserId());

                return ResponseEntity.ok(Map.of(
                        "totalCountComment",myPageCommentsList.getTotalElements()
                        ,"totalPage",myPageCommentsList.getTotalPages(),
                        "comments",myPageCommentsList.get().toList())
                );
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }

    @GetMapping("/someone/post/comment/paging")
    public ResponseEntity<?> getMyPagePostsAndComments(HttpServletRequest request,
                                                       @RequestParam String username,
                                                       @RequestParam int page,
                                                       @RequestParam int size) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                User user = userService.findByUser(username);
                Page<PostsAndCommentsProjection> postsCommentedByUser =
                        myPageService.getPostsCommentedByUser(page, size, user.getUserId());

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
