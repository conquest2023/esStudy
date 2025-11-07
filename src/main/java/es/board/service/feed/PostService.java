package es.board.service.feed;

import es.board.controller.model.dto.feed.PostDTO;
import es.board.repository.entity.PostEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {



    void savePost(PostDTO.Response feedSaveDTO);


    Page<PostEntity> getPosts(int page, int size);


    PostDTO.Request getPostDetail(int id);
}
