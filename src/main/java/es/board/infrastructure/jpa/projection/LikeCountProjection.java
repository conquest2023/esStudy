package es.board.infrastructure.jpa.projection;

public interface LikeCountProjection {
    Long getTargetId();
    Long getCount();
}
