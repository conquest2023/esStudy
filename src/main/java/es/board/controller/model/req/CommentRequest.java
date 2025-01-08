package es.board.controller.model.req;

import es.board.repository.document.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRequest {


    private String CommentUID;

    private String feedUID;

    private String username;

    private  String userId;


    private String content;

    private  int LikeCount;

    private  boolean anonymous;

    private LocalDateTime createdAt;






    public List<CommentRequest> changeCommentListDTO(List<Comment> comment){

        return comment.stream()
                .map(comment1 -> CommentRequest.builder()
                        .feedUID(comment1.getFeedUID())
                        .CommentUID(comment1.getCommentUID())
                        .userId(comment1.getCommentUID())
                        .username(comment1.getUsername())
                        .content(comment1.getContent())
                        .LikeCount(comment1.getLikeCount())
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
