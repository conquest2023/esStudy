package es.board.repository.entity.repository.infrastructure.feed;

import es.board.repository.entity.ReplyEntity;
import es.board.repository.entity.repository.infrastructure.ReplyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
        return repository.findReplys(id);
    }

    @Override
    public Optional<ReplyEntity> isExist(long id) {
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