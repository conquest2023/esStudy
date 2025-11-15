package es.board.service.feed;

import es.board.controller.model.dto.feed.LikeDto;
import es.board.repository.entity.repository.infrastructure.projection.LikeCountProjection;
import es.board.service.domain.Like;

import java.util.List;
import java.util.Map;

public interface LikeService {


     Map<Integer, Long> findPostPagingLikes(int page, int size);



    void toggleLike(String userId, LikeDto.Response like);

//    List<Lik>

    List<LikeDto.Request> findLikeFeedDetail(int id,String userId);

    List<LikeCountProjection> findLikeFeedDetailCount(int id);

}
