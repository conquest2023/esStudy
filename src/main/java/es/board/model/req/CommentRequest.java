package es.board.model.req;

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

    private String username;

    private String content;

    private  int LikeCount;

    private LocalDateTime createdAt;






    public List<CommentRequest> changeCommentToDTO(List<Comment> comment){

        return comment.stream()
                .map(comment1 -> CommentRequest.builder()
                        .CommentUID(comment1.getCommentUID())
                        .username(comment1.getUsername())
                        .content(comment1.getContent())
                        .LikeCount(comment1.getLikeCount())
                        .createdAt(comment1.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }



}
