package es.board.repository.entity.repository.infrastructure.feed;

import es.board.repository.entity.CommentEntity;
import es.board.repository.entity.ReplyEntity;
import es.board.repository.entity.repository.infrastructure.CommentJpaRepository;
import es.board.repository.entity.repository.infrastructure.ReplyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReplyAdapterRepository implements ReplyRepository {

    private final ReplyJpaRepository repository;

    @Override
    public void saveReply(ReplyEntity reply) {

        repository.save(reply);
    }

    @Override
    public List<ReplyEntity> findByReplys(int id) {
        return repository.findById(id);
    }

    @Override
    public Page<ReplyEntity> findByPageReplys(Pageable pageable) {
        return null;
    }

    @Override
    public ReplyEntity findReplyDetail(long id) {
        return null;
    }

    @Override
    public void deleteReply(long id) {
        repository.deleteById(id);
    }
}