package es.board.service;

import es.board.controller.model.req.InterviewQuestionRequest;
import es.board.controller.model.res.InterviewAnswerDTO;
import es.board.repository.entity.InterviewQuestion;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InterviewService {

    String getTodayQuestion();

    Page<InterviewQuestion> getCategoryQuestion(String category, String subCategory, int start, int end);

    List<InterviewQuestionRequest> getRandomQuestions();


    List<InterviewAnswerDTO> getBestAnswer(List<String> ids);


    void saveQuestion(InterviewAnswerDTO dto,String userId,String username);
}
