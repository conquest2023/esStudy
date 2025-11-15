package es.board.controller.feed;


import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.dto.feed.LikeDto;
import es.board.repository.entity.repository.infrastructure.projection.LikeCountProjection;
import es.board.service.feed.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    private final JwtTokenProvider provider;
    @GetMapping("/likes")
    public ResponseEntity<?> getPostPagingList(
                                               @RequestParam int page,
                                               @RequestParam int size){
        Map<Integer, Long> postPagingLikes = likeService.findPostPagingLikes(page, size);

        return ResponseEntity.ok(Map.of("likes",postPagingLikes));
    }

    @PostMapping("/like")
    public ResponseEntity<?> savePostLike(@RequestBody LikeDto.Response response,
                                          @RequestHeader(value = "Authorization", required = false) String token
    ){

        String userId = checkToken(token);
        likeService.toggleLike(userId, response);

        return ResponseEntity.ok(Map.of("ok","좋아요를 누르셨습니다"));
    }
    @GetMapping("/like/detail/{id}")
    public ResponseEntity<?> getLikeDetailFeed(@PathVariable int id,
                                               @RequestHeader(value = "Authorization", required = false) String token){
        String userId = checkToken(token);
        List<LikeDto.Request> likeFeedDetail = likeService.findLikeFeedDetail(id,userId);
        return ResponseEntity.ok(Map.of("likes",likeFeedDetail));
    }

    @GetMapping("/like/count/{id}")
    public ResponseEntity<?> getLikeDetailFedCount(@PathVariable int id){
        List<LikeCountProjection> likeFeedDetailCount = likeService.findLikeFeedDetailCount(id);
        log.info(likeFeedDetailCount.toString());
        return ResponseEntity.ok(Map.of("likes",likeFeedDetailCount));
    }

    @Nullable
    private String checkToken(String token) {
        String currentUserId = (token != null && token.startsWith("Bearer "))
                ? provider.getUserId(token.substring(7))
                : null;
        return currentUserId;
    }
}
