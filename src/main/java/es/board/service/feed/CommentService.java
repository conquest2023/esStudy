package es.board.service.feed;

import es.board.controller.model.dto.feed.CommentDTO;

import java.util.List;

public interface CommentService {


    void saveComment(String userId, CommentDTO.Response res);


    List<CommentDTO.Request> getComments(String userId, int id);

    void getComment(int id);

    CommentDTO.Request updateComment(long id,CommentDTO.Update update);

    void deleteComment(long id);
}
