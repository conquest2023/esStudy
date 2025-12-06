package es.board.infrastructure.jpa.projection;

import java.time.LocalDateTime;

public interface MyCommentProjection {

    Integer getPostId();

    String getUsername();

    String getContent();

    Integer getLikeCount();
    LocalDateTime getCreatedAt();

}
