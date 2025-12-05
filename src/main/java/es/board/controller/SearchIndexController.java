package es.board.controller;


import es.board.controller.model.dto.feed.PostDTO;
import es.board.infrastructure.es.SearchIndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchIndexController {

    private final SearchIndexService searchIndexService;

//    @GetMapping("/index/{title}")
//    public ResponseEntity<?> getSearchTitleIndex(){
//
//        return  null;
//
//    }

    @GetMapping("/search/post/{content}")
    public ResponseEntity<?> getSearchPostIndex(@PathVariable String  content){
        List<PostDTO.Response> searchContent = searchIndexService.getSearchPost(content);
        return ResponseEntity.ok(Map.of("content",searchContent));
    }

    @GetMapping("/search/content/{content}")
    public ResponseEntity<?> getSearchContentIndex(@PathVariable String  content){
        List<PostDTO.Response> searchContent = searchIndexService.getSearchContent(content);
        return ResponseEntity.ok(Map.of("content",searchContent));
    }
    @GetMapping("/search/title/{title}")
    public ResponseEntity<?> getSearchTitleIndex(@PathVariable String title){
        List<PostDTO.Response> searchContent = searchIndexService.getSearchTitle(title);
        return ResponseEntity.ok(Map.of("content",searchContent));
    }

    @GetMapping("/search/user/{username}")
    public ResponseEntity<?> getSearchUsername(@PathVariable String username){
        List<PostDTO.Response> searchContent = searchIndexService.getSearchUser(username);
        return ResponseEntity.ok(Map.of("content",searchContent));
    }

//    @GetMapping("/search/{reply}")
//    public ResponseEntity<?> getSearchReplyIndex(){
//
//        return null;
//    }
//
//    @GetMapping("/search/{comment}")
//    public ResponseEntity<?> getSearchCommentIndex(){
//
//        return null;
//    }
}
