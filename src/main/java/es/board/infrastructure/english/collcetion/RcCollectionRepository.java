package es.board.infrastructure.english.collcetion;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RcCollectionRepository extends MongoRepository<English_RC, String> {
    List<English_RC> findByIdIn(List<String> ids);

}