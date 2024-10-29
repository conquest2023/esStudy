package es.board.repository;

import es.board.model.req.ReqFeedDTO;
import es.board.repository.entity.Board;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BoardRepository extends ElasticsearchRepository<Board,String> {

    List<ReqFeedDTO> findAllById(String id);


    @Query("select new es.board.model.req.ReqFeedDTO(m.id,m.username,m.title,m.description from Board m")
    List<ReqFeedDTO> findAllFeed();

}
