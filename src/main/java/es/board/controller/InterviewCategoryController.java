package es.board.controller;


import es.board.repository.document.Like;
import es.board.repository.entity.InterviewQuestion;
import es.board.service.InterviewService;
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
@RequiredArgsConstructor
@RequestMapping("/api")
public class InterviewCategoryController {


    private  final InterviewService interviewService;


    @GetMapping("/interview/{category}/{subCategory}")
    public ResponseEntity<?> getInterviewData(@PathVariable String category,
                                              @PathVariable String subCategory){


        List<InterviewQuestion> interviewQuestionList=interviewService.getCategoryQuestion(category,subCategory);

        return   ResponseEntity.ok(Map.of("interview",interviewQuestionList));
    }



}
