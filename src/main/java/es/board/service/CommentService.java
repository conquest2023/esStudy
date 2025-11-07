package es.board.service;

import es.board.controller.model.dto.feed.CommentDTO;

import java.util.List;

public interface CommentService {


    void saveComment(String userId, CommentDTO.Response res);


    List<CommentDTO.Request> getComments(int id);

    void getComment(int id);
}
