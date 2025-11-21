package es.board.service;

import es.board.repository.entity.feed.PostEntity;
import es.board.repository.entity.repository.infrastructure.projection.MyCommentProjection;
import es.board.repository.entity.repository.infrastructure.projection.PostsAndCommentsProjection;
import org.springframework.data.domain.Page;

public interface MyPageService {


    int getSumPointUser(String userId);


    Page<PostEntity> getMyPageFeedList(int page, int size, String userId);

    Page<MyCommentProjection> getMyPageCommentList(int page, int size,String userId);


    Page<PostsAndCommentsProjection> getPostsCommentedByUser(int page, int size, String userId);

}
