package es.board.repository.entity.repository.infrastructure.feed;

import es.board.repository.entity.feed.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PostQueryRepository {


    Page<Integer>  findIds(Pageable pageable);
    List<Integer> findPostIds(int page,int size);

    List<Integer> findBestWeekPostIds(int page,int size,LocalDateTime lastSevenDays);
    PostEntity findPostDetail(int id);

    Page<PostEntity> findByMyPageUserPosts(Pageable pageable, String userId);

    Page<PostEntity> findByPagePosts(Pageable pageable);

    Page<PostEntity> findPopularPostsInLast7Week(Pageable pageable, LocalDateTime lastSevenDays);


}
