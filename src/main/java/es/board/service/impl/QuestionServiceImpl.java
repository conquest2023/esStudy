package es.board.service.impl;


import es.board.controller.model.mapper.CertificateMapper;
import es.board.controller.model.dto.certificate.QuestionPracticalDTO;
import es.board.repository.entity.repository.QuestionPracticalRepository;
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
    public List<QuestionPracticalDTO> getQuestionPracticalList(String category, String type) {
        return  certificateMapper.fromPracticalDTO(questionPracticalRepository.findByPracticalCategory(category,type));
    }

    @Override
    public List<QuestionPracticalDTO> getRandomPracticalList() {
        return  certificateMapper.fromPracticalDTO(questionPracticalRepository.findRandomPracticalCategory());
    }

    @Override
    public List<QuestionPracticalDTO> getTagPractical(Long tagId) {
        return  certificateMapper.fromPracticalDTO(questionPracticalRepository.findByTagId(tagId));
    }
}
