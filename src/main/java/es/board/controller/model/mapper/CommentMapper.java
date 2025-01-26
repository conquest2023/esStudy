package es.board.controller.model.mapper;


import es.board.controller.model.req.CommentRequest;
import es.board.repository.document.Comment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {

    public List<CommentRequest> changeCommentListDTO(List<Comment> comment){

        return comment.stream()
                .map(comment1 -> CommentRequest.builder()
                        .feedUID(comment1.getFeedUID())
                        .CommentUID(comment1.getCommentUID())
                        .userId(comment1.getUserId())
                        .username(comment1.getUsername())
                        .content(comment1.getContent())
                        .LikeCount(comment1.getLikeCount())
                        .createdAt(comment1.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }


    public List<CommentRequest> isAuthorCommentList(List<Comment> comment,boolean isAuthor){
        return comment.stream()
                .map(comment1 -> CommentRequest.builder()
                        .feedUID(comment1.getFeedUID())
                        .CommentUID(comment1.getCommentUID())
                        .userId(comment1.getUserId())
                        .username(comment1.getUsername())
                        .content(comment1.getContent())
                        .LikeCount(comment1.getLikeCount())
                        .isAuthor(isAuthor)
                        .createdAt(comment1.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public  CommentRequest changeCommentDTO(Comment comment){

        return CommentRequest.builder()
                .CommentUID(comment.getCommentUID())
                .username(comment.getUsername())
                .content(comment.getContent())
                .build();

    }
}
