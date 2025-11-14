package es.board.repository.entity.repository.infrastructure;

import es.board.repository.entity.repository.infrastructure.feed.CommentAggView;
import es.board.repository.entity.repository.infrastructure.feed.PostRepository;
import es.board.repository.entity.PostEntity;
import es.board.repository.entity.repository.infrastructure.feed.ReplyAggView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class PostAdapterRepository implements PostRepository {

    private final PostJpaRepository repository;

    public PostAdapterRepository(PostJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public String findByUserId(int id) {
        return repository.findByUserId(id);
    }

    @Override
    public void savePost(PostEntity post) {
        repository.save(post);
    }

    @Override
    public Optional<PostEntity> findById(int id) {
        return repository.findById(id);
    }

    @Override
    public PostEntity save(PostEntity post) {
        return repository.save(post);
    }

    @Override
    public void increaseViewCount(int postId) {

        repository.incrementViewCount(postId);
    }

    @Override
    public void deletePost(int id) {
        repository.deleteById(id);
    }



}
