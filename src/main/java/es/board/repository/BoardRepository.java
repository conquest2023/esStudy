package es.board.repository;

import es.board.model.req.ReqFeedDTO;
import es.board.repository.entity.Board;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BoardRepository extends ElasticsearchRepository<Board,String> {

    Board  findAllById(String id);

    @Query("{ \"match_all\": {} }")
    List<Board> findAllBy();

}
