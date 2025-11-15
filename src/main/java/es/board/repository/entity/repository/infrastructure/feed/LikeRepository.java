package es.board.repository.entity.repository.infrastructure.feed;

import es.board.repository.entity.LikeEntity;
import es.board.repository.entity.repository.infrastructure.projection.LikeCountProjection;
import es.board.service.domain.enum_type.TargetType;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LikeRepository {

    void save(LikeEntity likeEntity);

    void delete(long id);
    Optional<LikeEntity> existsByUserIdAndPostLike(String userId, int postId,TargetType targetType);

    Map<Integer, Long> findPagingPosts(Pageable pageable);


    List<LikeEntity> findLikeFeedDetail(int id);

    List<LikeCountProjection> findLikeFeedDetailCount(int id);
}
