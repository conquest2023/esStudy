package es.board.infrastructure.jpa.projection;

public interface LikeCountPostProjection {
    String getUsername();
    Long getCount();
}
