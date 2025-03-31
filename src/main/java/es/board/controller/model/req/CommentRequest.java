package es.board.controller.model.req;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.board.filter.XssSafeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRequest {


    private String CommentUID;

    private String feedUID;

    @JsonSerialize(using = XssSafeSerializer.class)
    private String username;

    @JsonIgnore
    private  String userId;

    @JsonSerialize(using = XssSafeSerializer.class)
    private String content;

    private  int LikeCount;

    private  boolean isAuthor;

    private  boolean  isCommentOwner;

    private  boolean anonymous;


    private LocalDateTime createdAt;






}
