package es.board.repository.entity.repository.infrastructure.projection;

import java.time.LocalDateTime;

public interface MyCommentProjection {

    Integer getPostId();

    String getUsername();

    String getContent();

    Integer getLikeCount();
    LocalDateTime getCreatedAt();

}
