package es.board.infrastructure.projection;

public interface LikeCountProjection {
    Long getTargetId();
    Long getCount();
}
