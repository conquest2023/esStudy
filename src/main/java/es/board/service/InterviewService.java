package es.board.service;

import es.board.controller.model.req.InterviewQuestionDTO;
import es.board.controller.model.res.InterviewAnswerDTO;

import java.util.List;

public interface InterviewService {

    String getTodayQuestion();

    List<InterviewQuestionDTO> getRandomQuestions();


    List<InterviewAnswerDTO> getBestAnswer(List<String> ids);


    void saveQuestion(InterviewAnswerDTO dto,String userId,String username);
}
