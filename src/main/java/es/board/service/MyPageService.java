package es.board.service;

import es.board.controller.model.dto.feed.CommentDTO;
import es.board.controller.model.dto.feed.PostDTO;
import es.board.repository.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MyPageService {


    int getSumPointUser(String userId);


    Page<PostEntity> getMyPageFeedList(int page, int size, String userId);

    Page<CommentDTO.Request> getMyPageCommentList(String userId);


    Page<PostDTO.Request> getPostsCommentedByUser(String userId);

}
