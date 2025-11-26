package es.board.infrastructure;

import es.board.domain.PostRepository;
import es.board.infrastructure.entity.feed.PostEntity;

import java.util.*;

public class PostTestRepository implements PostRepository {

    private Map<Integer,PostEntity> stores =new HashMap<>();

    @Override
    public String findByUserId(int id) {
        return null;
    }

    @Override
    public PostEntity savePost(PostEntity post) {
        return stores.put(post.getId(),post);
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
