package es.board.repository;


public interface LikeDAO {

    int saveLike(String feedUID);

    int saveCommentLike(String commentUID);

    int cancelLike(String feedUID);

}
