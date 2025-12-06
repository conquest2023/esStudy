package es.board.domain;

import es.board.infrastructure.entity.feed.LikeEntity;
import es.board.infrastructure.entity.user.User;
import es.board.infrastructure.feed.LikeAggView;
import es.board.infrastructure.jpa.projection.LikeCountProjection;
import es.board.domain.enum_type.TargetType;
import es.board.infrastructure.jpa.projection.PostWithLikeCount;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface LikeRepository {

    void save(LikeEntity likeEntity);

    User findByLikeUser(String userId, TargetType type);

    Page<PostWithLikeCount> findByPostWithLikeCountDESC(int page , int size);
    Optional<LikeEntity> findById(long id);
    void delete(long id);
    Optional<LikeEntity> existsByUserIdAndPostLike(String userId, int postId,long id);

    List<LikeAggView> findPagingLikes(List<Integer> ids);


    List<LikeEntity> findLikeFeedDetail(int id);

    List<LikeCountProjection> findLikeFeedDetailCount(int id);
}
