package es.board.service.feed;

import es.board.controller.model.dto.feed.LikeDto;
import es.board.controller.model.mapper.LikeMapper;
import es.board.repository.entity.feed.LikeEntity;
import es.board.repository.entity.repository.infrastructure.feed.LikeAggView;
import es.board.repository.entity.repository.infrastructure.feed.LikeRepository;
import es.board.repository.entity.repository.infrastructure.feed.PostQueryRepository;
import es.board.repository.entity.repository.infrastructure.projection.LikeCountProjection;
import es.board.service.domain.Like;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService{

    private final LikeRepository likeRepository;

    private final PostQueryRepository postRepository;
    @Override
    public Map<Integer, Long> findPostPagingLikes(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Integer> ids = postRepository.findPostIds(page,size);
//        List<LikeAggView> findPagingLikes(List<Integer> ids)
//        return pagingLikes;
        return  null;
    }
    @Override
    @Transactional
    public void toggleLike(String userId, LikeDto.Response like) {
        Optional<LikeEntity> likeEntity = likeRepository.existsByUserIdAndPostLike(userId, like.getPostId(),like.getTargetType());
        if(likeEntity.isPresent()) {
            log.info(likeEntity.get().getId().toString());
            likeRepository.delete(likeEntity.get().getId());
            log.info("좋아요가 삭제 되었습니다={}",likeEntity.get().getUserId());
            return;
        }
        Like domain = Like.of(like.getPostId(),userId, like.getTargetId(), like.getTargetType(), LocalDateTime.now());
        LikeEntity entity = LikeMapper.toEntity(domain);
        likeRepository.save(entity);
    }

    @Override
    public List<LikeDto.Request> findLikeFeedDetail(int id,String userId) {

        List<LikeEntity> likes = likeRepository.findLikeFeedDetail(id);
        List<Like> domainList = LikeMapper.toDomainList(likes);
        return  LikeMapper.toRequestList(domainList,userId);
    }

    @Override
    public List<LikeCountProjection> findLikeFeedDetailCount(int id) {
        return likeRepository.findLikeFeedDetailCount(id);
    }
}
