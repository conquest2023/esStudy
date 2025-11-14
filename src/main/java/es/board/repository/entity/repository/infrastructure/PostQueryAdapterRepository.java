package es.board.repository.entity.repository.infrastructure;

import es.board.repository.entity.PostEntity;
import es.board.repository.entity.repository.infrastructure.feed.CommentAggView;
import es.board.repository.entity.repository.infrastructure.feed.PostQueryRepository;
import es.board.repository.entity.repository.infrastructure.feed.ReplyAggView;
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
    public Page<PostEntity> findByPagePosts(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public PostEntity findPostDetail(int id) {
        Optional<PostEntity> post = repository.findById(id);
        return post.get();
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
