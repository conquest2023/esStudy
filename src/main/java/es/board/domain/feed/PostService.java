package es.board.domain.feed;

import es.board.controller.model.dto.PostDetailResponse;
import es.board.controller.model.dto.feed.PostDTO;
import es.board.infrastructure.entity.feed.PostEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {

    List<PostDTO.Response> getRecommendPost(List<Integer> ids);

    void incrementViewCount(int postId,String userId);


    PostDTO.Response updatePost(int id, String userId, PostDTO.Update update);

    void savePost(String userId, PostDTO.Request feedSaveDTO);

    Page<Integer> findIds(int page ,int size);

    Page<PostEntity> findCategoryPost(String category, int page, int size);

    void deletePost(int id);

    Page<PostEntity> findPostPagingList(int page, int size);


    PostDetailResponse findPostDetail(String userId, int id);

}
