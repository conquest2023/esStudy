package es.board.repository.entity.repository.infrastructure;

import es.board.repository.entity.repository.infrastructure.feed.PostRepository;
import es.board.repository.entity.feed.PostEntity;

import java.util.*;

public class PostTestRepository implements PostRepository {

    private Map<Integer,PostEntity> stores =new HashMap<>();

    @Override
    public String findByUserId(int id) {
        return null;
    }

    @Override
    public void savePost(PostEntity post) {
        stores.put(post.getId(),post);

    }

    @Override
    public void deletePost(int id) {

    }

    @Override
    public Optional<PostEntity> findById(int id) {
        return Optional.empty();
    }

    @Override
    public PostEntity save(PostEntity post) {
        return null;
    }

    @Override
    public void increaseViewCount(int postId) {

    }


}
