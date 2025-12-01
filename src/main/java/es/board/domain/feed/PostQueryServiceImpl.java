package es.board.domain.feed;

import es.board.infrastructure.entity.feed.PostEntity;
import es.board.infrastructure.feed.PostQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostQueryServiceImpl implements PostQueryService{

    private final PostQueryRepository queryRepository;

    @Override
    public Page<PostEntity> findPopularPostsInLast7Weeks(int page, int size) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastSevenDay = now.minusDays(7);
        Page<PostEntity> byMyPageUserPosts = queryRepository.findPopularPostsInLast7Week(page,size, lastSevenDay);
        return byMyPageUserPosts;
    }

    @Override
    public Page<PostEntity> findPopularMonthPosts(int page, int size) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonth = now.minusMonths(1);
        return queryRepository.findBestMonthPosts(page,size, lastMonth);
    }

    @Override
    public Page<PostEntity> findPopularTodayPosts(int page, int size) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastDay = now.minusDays(1);
        return queryRepository.findBestMonthPosts(page,size, lastDay);
    }
}
