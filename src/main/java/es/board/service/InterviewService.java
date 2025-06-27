package es.board.service;

import es.board.controller.model.req.InterviewQuestionRequest;
import es.board.controller.model.res.InterviewAnswerDTO;
import es.board.repository.entity.InterviewQuestion;

import java.util.List;

public interface InterviewService {

    String getTodayQuestion();

    List<InterviewQuestion> getCategoryQuestion(String category, String subCategory);

    List<InterviewQuestionRequest> getRandomQuestions();


    List<InterviewAnswerDTO> getBestAnswer(List<String> ids);


    void saveQuestion(InterviewAnswerDTO dto,String userId,String username);
}
