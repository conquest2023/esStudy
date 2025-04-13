package es.board.service.impl;


import es.board.controller.model.mapper.CertificateMapper;
import es.board.controller.model.req.QuestionPracticalDto;
import es.board.repository.entity.entityrepository.QuestionPracticalRepository;
import es.board.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private  final QuestionPracticalRepository questionPracticalRepository;

    private  final CertificateMapper certificateMapper;




    @Override
    public List<QuestionPracticalDto> getQuestionPracticeList(String category,String type) {
        return  certificateMapper.fromPracticalDTO(questionPracticalRepository.findByPracticalCategory(category,type));
    }

    @Override
    public List<QuestionPracticalDto> getRandomPracticeList() {
        return  certificateMapper.fromPracticalDTO(questionPracticalRepository.findRandomPracticalCategory());
    }
}
