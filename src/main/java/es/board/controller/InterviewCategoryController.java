package es.board.controller;


import es.board.repository.document.Like;
import es.board.repository.entity.InterviewQuestion;
import es.board.service.InterviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class InterviewCategoryController {


    private  final InterviewService interviewService;


    @GetMapping("/interview/{category}/{subCategory}")
    public ResponseEntity<?> getInterviewData(@PathVariable String category,
                                              @PathVariable String subCategory,
                                              @RequestParam int page,
                                              @RequestParam int size){


        Page<InterviewQuestion> result=interviewService.getCategoryQuestion(category,subCategory,page,size);

        List<InterviewQuestion> content = result.getContent();
        int totalPages = result.getTotalPages();
        long totalElements = result.getTotalElements();
        return   ResponseEntity.ok(Map.of("interview",content,
                                          "totalPages",totalPages,
                                          "totalElements",totalElements));
    }



}
