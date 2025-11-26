package es.board.infrastructure;

import es.board.infrastructure.entity.feed.PostEntity;
import es.board.infrastructure.feed.PostQueryRepository;
import es.board.infrastructure.jpa.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostQueryAdapterRepository implements PostQueryRepository {

    private final PostJpaRepository repository;


    @Override
    public Page<PostEntity> findByMyPageUserPosts(int page,int size, String userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return repository.findByMyPageUserPosts(pageable,userId);
    }

    @Override
    public Page<Integer> findIds(Pageable pageable) {
        return  repository.findIds(pageable);
    }

    @Override
    public Page<PostEntity> findPopularPostsInLast7Week(Pageable pageable, LocalDateTime lastSevenDays) {
        return repository.findPopularPostsInLast7Week(pageable,lastSevenDays);
    }

    @Override
    public List<Integer> findPostIds(int offset,int size) {
        return repository.findPostIds(offset,size);
    }

    @Override
    public List<Integer> findBestWeekPostIds(int page, int size, LocalDateTime lastSevenDays) {
        return repository.findBestWeekPostIds(page,size,lastSevenDays);
    }

    @Override
    public Page<PostEntity> findByPagePosts(Pageable pageable) {
        return repository.findPostPagingList(pageable);
    }


    @Override
    public PostEntity findPostDetail(int id) {
        Optional<PostEntity> post = repository.findById(id);
        return post.get();
    }

}
