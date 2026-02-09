package es.board.infrastructure.english.collcetion;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProblemRepository extends MongoRepository<EnglishProblem, String> {
    List<EnglishProblem> findByPart(int part);
}