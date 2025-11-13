package es.board.controller;

import es.board.config.jwt.JwtTokenProvider;
import es.board.repository.entity.PostEntity;
import es.board.service.AuthService;
import es.board.service.MyPageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

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
                String userId = userService.findById(username);
                Map<String, Object> response = Map.of(
                        "username",username,
                        "userId",userId
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
    public ResponseEntity<?> getMyPagePoints(HttpServletRequest request,
                                             @RequestParam int page,
                                             @RequestParam int size) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                Page<PostEntity> myPageFeedList = myPageService.getMyPageFeedList(page, size, jwtTokenProvider.getUserId(token));
                return ResponseEntity.ok(Map.of(
                        "totalCountPost",myPageFeedList.getTotalElements()
                        ,"totalPage",myPageFeedList.getTotalPages(),
                        "posts",myPageFeedList.get().collect(Collectors.toList()))
                );
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }


}
