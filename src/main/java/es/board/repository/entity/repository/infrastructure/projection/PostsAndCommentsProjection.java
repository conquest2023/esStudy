package es.board.repository.entity.repository.infrastructure.projection;

import java.time.LocalDateTime;

public interface PostsAndCommentsProjection {
    Integer getId();

    String getUsername();

    String getTitle();


    String getDescription();

    Integer getViewCount();

    Integer getLikeCount();

    LocalDateTime getCreatedAt();
}
