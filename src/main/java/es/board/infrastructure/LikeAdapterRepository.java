package es.board.infrastructure;

import es.board.infrastructure.entity.user.User;
import es.board.infrastructure.feed.LikeAggView;
import es.board.infrastructure.jpa.LikeJpaRepository;
import es.board.infrastructure.entity.feed.LikeEntity;
import es.board.infrastructure.jpa.projection.LikeCountProjection;
import es.board.domain.LikeRepository;
import es.board.domain.enum_type.TargetType;
import es.board.infrastructure.jpa.projection.PostWithLikeCount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LikeAdapterRepository implements LikeRepository {

    private final LikeJpaRepository likeRepository;

    @Override
    public void save(LikeEntity likeEntity) {
        likeRepository.save(likeEntity);
    }

    @Override
    public User findByLikeUser(String userId, TargetType type) {
        return likeRepository.findByLikeUser(userId,type);
    }

    @Override
    public Optional<LikeEntity> findById(long id) {
        return likeRepository.findById((int) id);
    }

    @Override
    public void delete(long id) {
        likeRepository.deleteById(id);
    }

    @Override
    public Optional<LikeEntity> existsByUserIdAndPostLike(String userId, int postId,long targetId) {
         return likeRepository.countByUserIdAndPostId(userId, postId,targetId);
    }

    @Override
    public List<LikeAggView> findPagingLikes(List<Integer> ids) {

        return likeRepository.findPagingLikes(ids);
    }

    @Override
    public List<LikeEntity> findLikeFeedDetail(int id) {
        return likeRepository.findByFeedDetail(id);
    }

    @Override
    public List<LikeCountProjection> findLikeFeedDetailCount(int id) {
        return  likeRepository.countByPostGroupByTargetType(id);
    }
    @Override
    public Page<PostWithLikeCount> findByPostWithLikeCountDESC(int page , int size){
        Pageable pageable = PageRequest.of(page, size);
        return likeRepository.findByLikeCountDESC(pageable);
    }

}
