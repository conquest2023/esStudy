package es.board.repository.entity.repository.infrastructure.projection;

public interface LikeCountProjection {
    Long getTargetId();
    Long getCount();
}
