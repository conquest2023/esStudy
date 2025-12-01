package es.board.domain.feed;

import es.board.infrastructure.entity.feed.PostEntity;
import org.springframework.data.domain.Page;

public interface PostQueryService {



    Page<PostEntity> findPopularPostsInLast7Weeks(int page, int size);;

    Page<PostEntity> findPopularMonthPosts(int page, int size);;

    Page<PostEntity> findPopularTodayPosts(int page, int size);;


}
