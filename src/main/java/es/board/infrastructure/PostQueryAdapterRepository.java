package es.board.infrastructure;

import es.board.infrastructure.entity.feed.PostEntity;
import es.board.infrastructure.feed.PostQueryRepository;
import es.board.infrastructure.jpa.PostJpaRepository;
import es.board.repository.entity.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostQueryAdapterRepository implements PostQueryRepository {

    private final PostJpaRepository repository;

    private final PointHistoryRepository pointHistoryRepository;
    @Override
    public Page<PostEntity> findByMyPageUserPosts(int page,int size, String userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return repository.findByMyPageUserPosts(pageable,userId);
    }

    @Override
    public Page<PostEntity> findByCategoryPosts(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return  repository.findCategoryPosts(category,pageable);
    }

    @Override
    public Page<Integer> findIds(int page,int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return  repository.findIds(pageable);
    }
    @Override
    public Page<PostEntity> findPopularPostsInLast7Week(int page,int size, LocalDateTime lastSevenDays) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findPopularPostsInLast7Week(pageable,lastSevenDays);
    }

    @Override
    public List<Integer> findPostIds(int offset,int size) {
        return repository.findPostIds(offset,size);
    }

    @Override
    public List<PostEntity> findTodayTop3(LocalDateTime today) {
        return repository.findTodayTop3Native(today);
    }

    @Override
    public  Optional<PostEntity> findUserTopToday(LocalDateTime lastDay) {
        List<String> topUser = pointHistoryRepository.findTop1PointUser();
        return repository.findUserTopToday(topUser.get(2),lastDay);
    }

    @Override
    public List<Integer> findBestPostIds(int page, int size, LocalDateTime lastSevenDays) {

        return repository.findBestPostIds(page,size,lastSevenDays);
    }

    @Override
    public Page<PostEntity> findBestMonthPosts(int page, int size, LocalDateTime lastMonth) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findPopularMonthPosts(pageable,lastMonth);
    }

    @Override
    public Page<PostEntity> findBestTodayPosts(int page, int size, LocalDateTime today) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findPopularTodayPosts(pageable,today);
    }


    @Override
    public Page<PostEntity> findByPagePosts(int page,int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return repository.findPostPagingList(pageable);
    }


    @Override
    public PostEntity findPostDetail(int id) {
        Optional<PostEntity> post = repository.findById(id);
        return post.get();
    }

    @Override
    public List<PostEntity> findPostAndPollEntity(List<Integer> ids) {
        return repository.findPostAndPoll(ids);
    }

}
