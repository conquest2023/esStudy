package es.board.infrastructure.feed;

import es.board.infrastructure.entity.feed.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface PostQueryRepository {


    Page<Integer>  findIds(Pageable pageable);
    List<Integer> findPostIds(int page,int size);

    List<Integer> findBestWeekPostIds(int page,int size,LocalDateTime lastSevenDays);
    PostEntity findPostDetail(int id);

    Page<PostEntity> findByMyPageUserPosts(int page,int size, String userId);

    Page<PostEntity> findByPagePosts(Pageable pageable);

    Page<PostEntity> findPopularPostsInLast7Week(Pageable pageable, LocalDateTime lastSevenDays);


}
