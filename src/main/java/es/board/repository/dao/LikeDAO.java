package es.board.repository.dao;


import org.springframework.stereotype.Repository;

public interface LikeDAO {

    int saveLike(String feedUID);

    int cancelLike(String feedUID);

}
