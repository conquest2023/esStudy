package es.board.infrastructure.collcetion;

import es.board.infrastructure.collcetion.english.EnglishProblem;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProblemRepository extends MongoRepository<EnglishProblem, String> {
    List<EnglishProblem> findByPart(int part);
}