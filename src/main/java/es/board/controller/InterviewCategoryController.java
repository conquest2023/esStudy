package es.board.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class InterviewCategoryController {


    @GetMapping("/interview/data")
    public ResponseEntity<?> getInterviewData(@PathVariable String category){

        return  null;
    }

}
