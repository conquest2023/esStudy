package es.board.repository.entity.repository;


import es.board.repository.document.Comment;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository  extends ElasticsearchRepository<Comment,String> {

//    Comment findByCommentUID(String commentUID);

    @Query("{ \"match_all\": {} }")
    List<Comment> findAllCommentBy();

    @Query("{\"match\": {\"content\": \"?0\"}}")
    List<Comment> findCommentsByUsernameAndContent(String keyword);

    @Query("{ \"query\": { \"match\": { \"content\": \"?0\" } }, \"sort\": [ { \"_score\": { \"order\": \"desc\" } } ] }")
    List<Comment> findByContentMatchingQuery(String content);



}

