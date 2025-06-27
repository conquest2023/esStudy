package es.board.controller.model.mapper;


import es.board.controller.model.req.CommentRequest;
import es.board.controller.model.req.CommentUpdate;
import es.board.controller.model.res.CommentCreate;
import es.board.controller.model.res.FeedCreateResponse;
import es.board.repository.document.Comment;
import es.board.repository.entity.Notification;
import es.board.service.event.FeedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

    public Notification toCommentNotification(String userId, CommentCreate comment) {
        return Notification.builder()
                .userId(userId)
                .username(comment.getUsername())
                .sender(comment.getUserId())
                .feedUID(comment.getFeedUID())
                .message(comment.getContent())
                .type("댓글")
                .username(comment.getUsername())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Notification toCommentEvent(String userId, FeedEvent event) {
        return Notification.builder()
                .userId(userId)
                .username(event.getUsername())
                .sender(event.getUsername())
                .feedUID(event.getFeedUID())
                .message(event.getContent())
                .isCheck(false)
                .type("댓글")
                .username(event.getUsername())
                .createdAt(LocalDateTime.now())
                .build();
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
    public Comment convertDtoToEntity(CommentUpdate eq) {
        return  Comment.builder()
                .commentUID(eq.getCommentUID())
                .username(eq.getUsername())
                .content(eq.getContent())
                .build();
    }
}
