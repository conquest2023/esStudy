package es.board.repository;

import es.board.repository.document.Board;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends ElasticsearchRepository<Board,String> {

//    Board  findAllById(String id);

}
