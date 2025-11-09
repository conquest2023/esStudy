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
    public void savePost(PostEntity post) {
        repository.save(post);
    }

    @Override
    public List<PostEntity> findByPosts(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Integer> findIds(Pageable pageable) {
        return  repository.findIds(pageable);
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


    @Override
    public void deletePost(int id) {
        repository.deleteById(id);
    }

    @Override
    public Map<Integer, Long> countByReplyAndComment(List<Integer> ids) {
        Map<Integer, Long> comments = repository.countCommentsIn(ids).stream()
                .collect(Collectors.toMap(CommentAggView::getPostId, CommentAggView::getCnt));
        Map<Integer, Long> replies = repository.countRepliesIn(ids).stream()
                .collect(Collectors.toMap(ReplyAggView::getPostId, ReplyAggView::getCnt));
        Map<Integer, Long> map = new HashMap<>();
        for (Integer id : ids) {
            long commentId= comments.getOrDefault(id,0L);
            long replyId= replies.getOrDefault(id,0L);

            map.put(id,commentId+replyId);
        }
        return map;
    }
}
