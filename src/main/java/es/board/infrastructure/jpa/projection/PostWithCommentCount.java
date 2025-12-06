package es.board.infrastructure.jpa.projection;

import java.time.LocalDateTime;

public interface PostWithCommentCount {
    Long getId();


    String getTitle();


    String getUsername();


    String getDescription();


    String getViewCount();


    LocalDateTime getCreatedAt();


    Long getCommentCount();
}