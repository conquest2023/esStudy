package es.board.model.req;

import es.board.repository.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqCommentDTO {


    private String CommentUID;

    private String username;


    private String content;

    private  int LikeCount;

    private LocalDateTime createdAt;






    public List<ReqCommentDTO> DTOFromEntity(List<Comment> comment){

        return comment.stream()
                .map(comment1 -> ReqCommentDTO.builder()
                        .CommentUID(comment1.getCommentUID())
                        .username(comment1.getUsername())
                        .content(comment1.getContent())
                        .LikeCount(comment1.getLikeCount())
                        .createdAt(comment1.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
