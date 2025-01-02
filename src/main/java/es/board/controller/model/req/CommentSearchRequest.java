package es.board.controller.model.req;

import es.board.repository.document.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentSearchRequest {

    private String username;

    private String content;

    public List<CommentSearchRequest> changeCommentToSearchDTO(List<Comment> comment){

        return comment.stream()
                .map(comment1 -> CommentSearchRequest.builder()
                        .username(comment1.getUsername())
                        .content(comment1.getContent())
                        .build())
                .collect(Collectors.toList());
    }



}
