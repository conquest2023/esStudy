package es.board.service;


import es.board.repository.entity.DailyQuestion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;

public interface DailyQuestionService {


    List<DailyQuestion> findToeicDailyQuestion(String category);


    List<DailyQuestion> findCivilDailyQuestion(String category);


    List<DailyQuestion> findPoliceDailyQuestion(String category);


}
