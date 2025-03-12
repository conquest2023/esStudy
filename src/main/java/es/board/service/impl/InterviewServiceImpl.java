package es.board.service.impl;

import es.board.controller.model.mapper.MainFunctionMapper;

import es.board.controller.model.req.InterviewQuestionDTO;

import es.board.controller.model.res.InterviewAnswerDTO;
import es.board.repository.entity.InterviewQuestion;
import es.board.repository.entity.entityrepository.InterviewQuestionAnswerRepository;
import es.board.repository.entity.entityrepository.InterviewQuestionRepository;
import es.board.service.InterviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewServiceImpl implements InterviewService {

    private final InterviewQuestionRepository questionRepository;

    private final StringRedisTemplate redisTemplate;

    private  final InterviewQuestionAnswerRepository answerRepository;

    private  final MainFunctionMapper mapper;

    private static final String INTERVIEW_CACHE_KEY = "daily_interview_question";


    @Override
    public String getTodayQuestion() {
        String question = redisTemplate.opsForValue().get(INTERVIEW_CACHE_KEY);
        if (question == null) {
            question = fetchRandomQuestion();
            redisTemplate.opsForValue().set(INTERVIEW_CACHE_KEY, question, Duration.ofDays(1));
        }

        return question;
    }


    @Override
    public List<InterviewQuestionDTO> getRandomQuestions() {
        return questionRepository.findRandomITAndGeneralQuestions().stream()
                .map(question -> InterviewQuestionDTO.builder()
                        .id(question.getId())
                        .question(question.getQuestion())
                        .category(question.getCategory())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void saveQuestion(InterviewAnswerDTO dto,String  userId) {
        answerRepository.save(mapper.toInterviewEntity(dto,userId));
    }

    private String fetchRandomQuestion() {
        int randomIndex = new Random().nextInt(322) + 1;
        return questionRepository.findById((long) randomIndex)
                .map(InterviewQuestion::getQuestion)
                .orElse("오늘의 질문을 찾을 수 없습니다.");
    }
}
