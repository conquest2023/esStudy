package es.board.repository.entity.repository.infrastructure;

import es.board.repository.entity.LikeEntity;
import es.board.repository.entity.PostEntity;
import es.board.repository.entity.repository.infrastructure.feed.*;
import es.board.repository.entity.repository.infrastructure.projection.LikeCountProjection;
import es.board.service.domain.enum_type.TargetType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
@Slf4j
@Repository
@RequiredArgsConstructor
public class LikeAdapterRepository implements LikeRepository {

    private final LikeJpaRepository likeRepository;

    private final PostQueryRepository postRepository;
    @Override
    public void save(LikeEntity likeEntity) {
        likeRepository.save(likeEntity);
    }

    @Override
    public void delete(long id) {
        likeRepository.deleteById(id);
    }

    @Override
    public Optional<LikeEntity> existsByUserIdAndPostLike(String userId, int postId,TargetType targetType) {
         return likeRepository.countByUserIdAndPostId(userId, postId,targetType);
    }

    @Override
    public Map<Integer, Long> findPagingPosts(Pageable pageable) {
        List<Integer> ids = postRepository.findPostIds(pageable);
        Map<Integer, Long> likes = likeRepository.findPagingPost(ids)
                .stream().collect(Collectors.toMap(LikeAggView::getPostId, LikeAggView::getCnt));
        Map<Integer, Long> map = new HashMap<>();
        for (Integer id : ids) {
            if(likes.containsKey(id)){
                map.put(id,likes.get(id));
            }
        }
        return map;
    }

    @Override
    public List<LikeEntity> findLikeFeedDetail(int id) {
        return likeRepository.findByFeedDetail(id);
    }

    @Override
    public List<LikeCountProjection> findLikeFeedDetailCount(int id) {
        return  likeRepository.countByPostGroupByTargetType(id);
    }
}
