package es.board.service;

import es.board.controller.model.req.QuestionPracticalDto;

import java.util.List;

public interface QuestionService {


    List<QuestionPracticalDto> getQuestionPracticalList(String category, String type);


    List<QuestionPracticalDto> getRandomPracticalList();

    List<QuestionPracticalDto> getTagPractical(Long tagId);

}
