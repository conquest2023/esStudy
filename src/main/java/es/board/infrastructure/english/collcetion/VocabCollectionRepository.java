package es.board.infrastructure.english.collcetion;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface VocabCollectionRepository extends MongoRepository<English_Vocab, String> {
    @Query("{'_id': {$in: ?0}}")
    List<English_Vocab> findProblemsByCustomIds(List<String> ids);
}