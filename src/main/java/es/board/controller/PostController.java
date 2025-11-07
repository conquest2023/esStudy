package es.board.controller;


import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.dto.feed.PostDTO;
import es.board.repository.entity.PostEntity;
import es.board.service.feed.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class PostController {

    private final JwtTokenProvider provider;

    private final PostService postService;

    @PostMapping("/post")
    public ResponseEntity<?> savePost(@RequestHeader(value = "Authorization", required = false) String token,
                                      @RequestPart("feed")
                                      PostDTO.Response response){
        postService.savePost(response);
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "message", "게시글이 정상적으로 저장되었습니다."
        ));
    }


    @GetMapping("/post/{id}")
    public ResponseEntity<?> getPost(@PathVariable int id){
        log.info(String.valueOf(id));
        PostDTO.Request postDetail = postService.getPostDetail(id);
        return ResponseEntity.ok(Map.of("ok",postDetail));
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

}
