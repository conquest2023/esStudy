package es.board.domain.english;

import es.board.infrastructure.collcetion.ProblemRepository;
import es.board.infrastructure.collcetion.english.EnglishProblem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnglishService {

    private final ProblemRepository problemRepository;


    public List<EnglishProblem> findProblemList(){
        return problemRepository.findAll();
    }

}
