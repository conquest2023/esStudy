package es.board.repository.entity.repository.infrastructure;

import es.board.repository.entity.repository.infrastructure.feed.PostRepository;
import es.board.repository.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public void increaseViewCount(int postId) {

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

    @Override
    public  Page<Integer>  findIds(Pageable pageable) {
        return null;
    }

    @Override
    public void deletePost(int id) {
        stores.remove(id);
    }

    @Override
    public Map<Integer, Long> countByReplyAndComment(List<Integer> ids) {
        return null;
    }
}
