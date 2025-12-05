package es.board.infrastructure.feed;

import es.board.infrastructure.entity.feed.PostEntity;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostQueryRepository {


    Page<Integer> findIds(int page,int size);
    List<Integer> findPostIds(int page,int size);

    List<PostEntity> findTodayTop3(LocalDateTime today);

    Optional<PostEntity> findUserTopToday(LocalDateTime lastDay);
    List<Integer> findBestPostIds(int page, int size, LocalDateTime lastSevenDays);
    PostEntity findPostDetail(int id);

    Page<PostEntity> findByMyPageUserPosts(int page,int size, String userId);


    Page<PostEntity> findByCategoryPosts(String category,int page,int size);

    Page<PostEntity> findByPagePosts(int page,int size);

    Page<PostEntity> findPopularPostsInLast7Week(int page,int size, LocalDateTime lastSevenDays);


    Page<PostEntity> findBestTodayPosts(int page, int size, LocalDateTime today);

    Page<PostEntity> findBestMonthPosts(int page, int size, LocalDateTime lastMonth);

}
