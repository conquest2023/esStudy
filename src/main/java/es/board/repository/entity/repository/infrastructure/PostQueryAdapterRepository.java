package es.board.repository.entity.repository.infrastructure;

import es.board.repository.entity.feed.PostEntity;
import es.board.repository.entity.repository.infrastructure.feed.CommentAggView;
import es.board.repository.entity.repository.infrastructure.feed.PostQueryRepository;
import es.board.repository.entity.repository.infrastructure.feed.ReplyAggView;
import es.board.repository.entity.repository.infrastructure.jpa.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostQueryAdapterRepository implements PostQueryRepository {

    private final PostJpaRepository repository;


    @Override
    public Page<PostEntity> findByMyPageUserPosts(Pageable pageable, String userId) {
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
        return repository.findAll(pageable);
    }


    @Override
    public PostEntity findPostDetail(int id) {
        Optional<PostEntity> post = repository.findById(id);
        return post.get();
    }

}
