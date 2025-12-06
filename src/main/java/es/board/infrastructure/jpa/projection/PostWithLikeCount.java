package es.board.infrastructure.jpa.projection;

import java.time.LocalDateTime;

public interface PostWithLikeCount {
    Long getId();


    String getTitle();


    String getUsername();


    String getDescription();


    String getViewCount();


    LocalDateTime getCreatedAt();


    Long getLikeCount();
}