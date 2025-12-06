package es.board.infrastructure.jpa.projection;

import java.time.LocalDateTime;

public interface PostWithReplyCount {
    Long getId();


    String getTitle();


    String getUsername();


    String getDescription();


    String getViewCount();


    LocalDateTime getCreatedAt();


    Long getReplyCount();
}