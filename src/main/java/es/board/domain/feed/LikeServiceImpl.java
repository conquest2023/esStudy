package es.board.domain.feed;

import es.board.controller.model.dto.feed.LikeDto;
import es.board.controller.model.mapper.LikeMapper;
import es.board.domain.event.LikeCreatedEvent;
import es.board.infrastructure.entity.feed.LikeEntity;
import es.board.domain.LikeRepository;
import es.board.infrastructure.jpa.projection.LikeCountProjection;
import es.board.domain.Like;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService{

    private final ApplicationEventPublisher eventPublisher;

    private final LikeRepository likeRepository;

    @Override
    public Map<Integer, Long> findPostPagingLikes(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
//        List<Integer> ids = postRepository.findPostIds(page,size);
//        List<LikeAggView> findPagingLikes(List<Integer> ids)
//        return pagingLikes;
        return  null;
    }
    @Override
    @Transactional
    public void toggleLike(String userId, LikeDto.Request like) {
        Optional<LikeEntity> likeEntity = likeRepository.existsByUserIdAndPostLike(userId, like.getPostId(),like.getTargetId());
        if(likeEntity.isPresent()) {
            likeRepository.delete(likeEntity.get().getId());
            log.info("좋아요가 삭제 되었습니다={}",likeEntity.get().getUserId());
            return;
        }
        Like domain = Like.of(like.getPostId(),userId, like.getTargetId(), like.getTargetType(), LocalDateTime.now());
        LikeEntity entity = LikeMapper.toEntity(domain);
        likeRepository.save(entity);
        eventPublisher.publishEvent(new LikeCreatedEvent(entity.getPostId(),userId,like));
    }

    @Override
    public List<LikeDto.Response> findLikeFeedDetail(int id, String userId) {

        List<LikeEntity> likes = likeRepository.findLikeFeedDetail(id);
        List<Like> domainList = LikeMapper.toDomainList(likes);
        return  LikeMapper.toResponseList(domainList,userId);
    }

    @Override
    public List<LikeCountProjection> findLikeFeedDetailCount(int id) {
        return likeRepository.findLikeFeedDetailCount(id);
    }
}
