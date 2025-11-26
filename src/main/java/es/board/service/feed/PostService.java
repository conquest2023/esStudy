package es.board.service.feed;

import es.board.controller.model.dto.PostDetailResponse;
import es.board.controller.model.dto.feed.PostDTO;
import es.board.repository.entity.feed.PostEntity;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface PostService {


    void incrementViewCount(int postId);

    Page<PostEntity> findPopularPostsInLast7Weeks(int page,int size);
    PostDTO.Response updatePost(int id, String userId, PostDTO.Update update);

    void savePost(String userId, PostDTO.Request feedSaveDTO);

    Page<Integer> findIds(int page ,int size);


    void deletePost(int id);

    Page<PostEntity> findPostPagingList(int page, int size);


    PostDetailResponse findPostDetail(String userId, int id);


    Map<Integer, Long> getCountByCommentAndReply(int page , int size);
}
