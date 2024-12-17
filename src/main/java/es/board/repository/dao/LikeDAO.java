package es.board.repository.dao;


import org.springframework.stereotype.Repository;

public interface LikeDAO {

    int saveLike(String feedUID);

    int saveCommentLike(String commentUID);

    int cancelLike(String feedUID);

}
