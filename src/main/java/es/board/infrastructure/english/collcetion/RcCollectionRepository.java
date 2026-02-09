package es.board.infrastructure.english.collcetion;

import org.springframework.beans.factory.parsing.Problem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface RcCollectionRepository extends MongoRepository<English_RC, String> {
    @Query("{'_id': {$in: ?0}}")
    List<English_RC> findProblemsByCustomIds(List<String> ids);
}