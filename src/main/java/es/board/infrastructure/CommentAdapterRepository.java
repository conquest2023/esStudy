package es.board.infrastructure;

import es.board.infrastructure.feed.CommentAggView;
import es.board.infrastructure.entity.feed.CommentEntity;
import es.board.domain.CommentRepository;
import es.board.infrastructure.jpa.CommentJpaRepository;
import es.board.infrastructure.projection.MyCommentProjection;
import es.board.infrastructure.projection.PostsAndCommentsProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentAdapterRepository implements CommentRepository {

    private final CommentJpaRepository repository;

    @Override
    public List<CommentAggView> countCommentsIn(List<Integer> postIds) {
         return repository.countCommentsIn(postIds);
    }

    @Override
    public void saveComment(CommentEntity comment) {

        repository.save(comment);
    }

    @Override
    public String findByUserId(long commentId) {
        return repository.findByUserId(commentId);
    }
    @Override
    public List<CommentEntity> findByComments(int id) {
        return repository.findById(id);
    }

    @Override
    public Optional<CommentEntity> isExist(long id) {

        return repository.findById(id);
    }

    @Override
    public Page<MyCommentProjection> findUserMyPageComments(int page,int size, String userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return repository.findByMyPageUserComments(pageable,userId);
    }

    @Override
    public Page<PostsAndCommentsProjection> findMyPagePostsAndComments(int page,int size, String userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return repository.findByMyPageUserPostsAndComments(pageable,userId);
    }

    @Override
    public void deleteComment(long id) {
        repository.deleteById(id);
    }
}