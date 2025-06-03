package es.board.service.impl;

import es.board.repository.entity.DailyQuestion;
import es.board.repository.entity.repository.DailyQuestionRepository;
import es.board.service.DailyQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
@RequiredArgsConstructor
public class DailyQuestionServiceImpl implements DailyQuestionService {


    private  final DailyQuestionRepository dailyQuestionRepository;

    @Override
    public List<DailyQuestion> findToeicDailyQuestion(String category) {
        return dailyQuestionRepository.findDailyQuestionByToeic(category);
    }
}
