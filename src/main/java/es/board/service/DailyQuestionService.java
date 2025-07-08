package es.board.service;


import es.board.controller.model.dto.feed.DailyCheckDTO;
import es.board.controller.model.dto.interview.DailyBookMark;
import es.board.repository.entity.DailyQuestion;

import java.util.List;

public interface DailyQuestionService {


    List<DailyQuestion> findToeicDailyQuestion(String category);


    List<DailyQuestion> findCivilDailyQuestion(String category);


    List<DailyQuestion> findPoliceDailyQuestion(String category);


    List<DailyQuestion> getDailyQuestion(String userId);


    boolean checkDailyAnswer(String userId, DailyCheckDTO request);


    void saveDailyBookMark(String userId, DailyBookMark daily);


}
