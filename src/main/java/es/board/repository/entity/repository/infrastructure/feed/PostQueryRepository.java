package es.board.repository.entity.repository.infrastructure.feed;

import es.board.repository.entity.feed.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PostQueryRepository {


    Page<PostEntity> findByMyPageUserPosts(Pageable pageable, String userId);



    Page<PostEntity> findByPagePosts(Pageable pageable);

    List<Integer> findPostIds(Pageable pageable);
    PostEntity findPostDetail(int id);

    Page<Integer>  findIds(Pageable pageable);

    Page<PostEntity> findPopularPostsInLast7Week(Pageable pageable, LocalDateTime lastSevenDays);

    Map<Integer, Long> countByReplyAndComment(List<Integer> ids);
}
