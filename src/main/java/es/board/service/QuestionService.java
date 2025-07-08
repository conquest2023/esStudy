package es.board.service;

import es.board.controller.model.dto.certificate.QuestionPracticalDTO;

import java.util.List;

public interface QuestionService {


    List<QuestionPracticalDTO> getQuestionPracticalList(String category, String type);


    List<QuestionPracticalDTO> getRandomPracticalList();

    List<QuestionPracticalDTO> getTagPractical(Long tagId);

}
