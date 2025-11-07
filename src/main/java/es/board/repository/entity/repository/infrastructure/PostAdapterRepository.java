package es.board.repository.entity.repository.infrastructure;

import es.board.repository.entity.repository.infrastructure.feed.PostRepository;
import es.board.repository.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PostAdapterRepository implements PostRepository {

    private final PostJpaRepository repository;

    public PostAdapterRepository(PostJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void savePost(PostEntity post) {
        repository.save(post);
    }

    @Override
    public List<PostEntity> findByPosts(Pageable pageable) {
        return null;
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
