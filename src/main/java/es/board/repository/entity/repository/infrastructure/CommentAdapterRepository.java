package es.board.repository.entity.repository.infrastructure;

import es.board.repository.entity.CommentEntity;
import es.board.repository.entity.PostEntity;
import es.board.repository.entity.repository.infrastructure.feed.CommentRepository;
import es.board.repository.entity.repository.infrastructure.feed.PostRepository;
import es.board.repository.entity.repository.infrastructure.projection.MyCommentProjection;
import es.board.repository.entity.repository.infrastructure.projection.PostsAndCommentsProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.xml.stream.events.Comment;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentAdapterRepository implements CommentRepository {

    private final CommentJpaRepository repository;
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
    public Page<MyCommentProjection> findUserMyPageComments(Pageable pageable, String userId) {
        return repository.findByMyPageUserComments(pageable,userId);
    }

    @Override
    public Page<PostsAndCommentsProjection> findMyPagePostsAndComments(Pageable pageable, String userId) {
        return repository.findByMyPageUserPostsAndComments(pageable,userId);
    }

    @Override
    public CommentEntity findCommentDetail(long id) {
        return null;
    }

    @Override
    public void deleteComment(long id) {
        repository.deleteById(id);
    }
}