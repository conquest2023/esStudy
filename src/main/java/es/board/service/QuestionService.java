package es.board.service;

import es.board.controller.model.req.QuestionPracticalDto;

import java.util.List;

public interface QuestionService {


    List<QuestionPracticalDto> getQuestionPracticeList(String category,String type);
}
