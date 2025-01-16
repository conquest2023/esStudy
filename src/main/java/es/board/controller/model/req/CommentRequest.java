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










}
