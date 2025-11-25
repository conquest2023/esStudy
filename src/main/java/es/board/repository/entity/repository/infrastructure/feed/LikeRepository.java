package es.board.repository.entity.repository.infrastructure.feed;

import es.board.repository.entity.feed.LikeEntity;
import es.board.repository.entity.repository.infrastructure.projection.LikeCountProjection;
import es.board.service.domain.enum_type.TargetType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LikeRepository {

    void save(LikeEntity likeEntity);

    void delete(long id);
    Optional<LikeEntity> existsByUserIdAndPostLike(String userId, int postId,TargetType targetType);

    List<LikeAggView> findPagingLikes(List<Integer> ids);


    List<LikeEntity> findLikeFeedDetail(int id);

    List<LikeCountProjection> findLikeFeedDetailCount(int id);
}
