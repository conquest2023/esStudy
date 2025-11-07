package es.board.repository.entity.repository.infrastructure;

import es.board.repository.entity.CommentEntity;
import es.board.repository.entity.PostEntity;
import es.board.repository.entity.repository.infrastructure.feed.CommentRepository;
import es.board.repository.entity.repository.infrastructure.feed.PostRepository;
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
    public List<CommentEntity> findByComments(int id) {
        return repository.findById(id);
    }

    @Override
    public Page<CommentEntity> findByPageComments(Pageable pageable) {
        return null;
    }

    @Override
    public CommentEntity findCommentDetail(long id) {
        return null;
    }
}