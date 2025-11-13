package es.board.service.feed;

import es.board.controller.model.dto.feed.PostDTO;
import es.board.repository.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface PostService {


    void incrementViewCount(int postId);

    PostDTO.Request updatePost(int id,String userId,PostDTO.Update update);

    void savePost(String userId, PostDTO.Response feedSaveDTO);

    Page<Integer> findIds(int page ,int size);


    void deletePost(int id);

    Page<PostEntity> getPosts(int page, int size);


    PostDTO.Request getPostDetail(String userId, int id);


    Map<Integer, Long> getCountByCommentAndReply(int page , int size);
}
