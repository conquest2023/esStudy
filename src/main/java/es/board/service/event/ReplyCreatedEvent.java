package es.board.service.event;

import es.board.controller.model.dto.feed.CommentDTO;
import es.board.controller.model.dto.feed.ReplyDTO;
import lombok.Getter;

@Getter
public class ReplyCreatedEvent {

    private final int postId;


    private final String userId;

    private final long commentId;

    private final ReplyDTO.Response response;

    public ReplyCreatedEvent(int postId, String userId, long commentId, ReplyDTO.Response response) {
        this.postId = postId;
        this.userId = userId;
        this.commentId = commentId;
        this.response = response;
    }




}
