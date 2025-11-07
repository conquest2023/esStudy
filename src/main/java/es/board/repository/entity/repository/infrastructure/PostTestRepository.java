package es.board.repository.entity.repository.infrastructure;

import es.board.repository.entity.repository.infrastructure.feed.PostRepository;
import es.board.repository.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostTestRepository implements PostRepository {

    private Map<Integer,PostEntity> stores =new HashMap<>();
    @Override
    public void savePost(PostEntity post) {
        stores.put(post.getId(),post);

    }

    @Override
    public List<PostEntity> findByPosts(Pageable pageable) {
        return null;
    }

    @Override
    public Page<PostEntity> findByPagePosts(Pageable pageable) {
        return null;
    }


    @Override
    public PostEntity findPostDetail(int id) {
        return null;
    }
}
