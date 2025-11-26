package es.board.infrastructure;

import es.board.domain.PostRepository;
import es.board.infrastructure.entity.feed.PostEntity;
import es.board.infrastructure.jpa.PostJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
    public PostEntity savePost(PostEntity post) {
        return repository.save(post);
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
