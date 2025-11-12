package es.board.service.event;

import es.board.controller.model.dto.feed.CommentDTO;
import lombok.Getter;

@Getter
public class CommentCreatedEvent {

    private final int postId;

    private final String userId;

    private final CommentDTO.Response response;


    public CommentCreatedEvent(int postId, String userId, CommentDTO.Response response) {
        this.postId = postId;
        this.userId = userId;
        this.response = response;
    }
}
