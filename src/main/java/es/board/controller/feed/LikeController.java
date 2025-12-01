package es.board.controller.feed;


import es.board.controller.model.dto.feed.LikeDto;
import es.board.infrastructure.projection.LikeCountProjection;
import es.board.domain.feed.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping("/likes")
    public ResponseEntity<?> getPostPagingList(
                                               @RequestParam int page,
                                               @RequestParam int size){
        Map<Integer, Long> postPagingLikes = likeService.findPostPagingLikes(page, size);

        return ResponseEntity.ok(Map.of("likes",postPagingLikes));
    }

    @PostMapping("/like")
    public ResponseEntity<?> savePostLike(@RequestBody LikeDto.Request response,
                                          @RequestAttribute("userId") String userId
    ){
        likeService.toggleLike(userId, response);

        return ResponseEntity.ok(Map.of("ok","좋아요를 누르셨습니다"));
    }

    @GetMapping("/like/detail/{id}")
    public ResponseEntity<?> getLikeDetailFeed(@PathVariable int id,
                                               @RequestAttribute(value = "userId",required = false) String userId){
        List<LikeDto.Response> likeFeedDetail = likeService.findLikeFeedDetail(id,userId);

        log.info(likeFeedDetail.toString());
        return ResponseEntity.ok(Map.of("likes",likeFeedDetail));
    }
    @GetMapping("/like/count/{id}")
    public ResponseEntity<?> getLikeDetailFedCount(@PathVariable int id){
        List<LikeCountProjection> likeFeedDetailCount = likeService.findLikeFeedDetailCount(id);
        return ResponseEntity.ok(Map.of("likes",likeFeedDetailCount));
    }

}
