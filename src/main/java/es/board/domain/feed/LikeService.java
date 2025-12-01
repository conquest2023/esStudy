package es.board.domain.feed;

import es.board.controller.model.dto.feed.LikeDto;
import es.board.infrastructure.projection.LikeCountProjection;

import java.util.List;
import java.util.Map;

public interface LikeService {


     Map<Integer, Long> findPostPagingLikes(int page, int size);



    void toggleLike(String userId, LikeDto.Request like);

//    List<Lik>

    List<LikeDto.Response> findLikeFeedDetail(int id, String userId);

    List<LikeCountProjection> findLikeFeedDetailCount(int id);

}
