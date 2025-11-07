package es.board.controller;


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


    private final PostService postService;

    @PostMapping("/post")
    public ResponseEntity<?> savePost(
                                      @RequestBody
                                      PostDTO.Response response){
        postService.savePost(response);
        return ResponseEntity.ok("ok");
    }


    @GetMapping("/post")
    public ResponseEntity<?> getPost(
            Long id){
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
