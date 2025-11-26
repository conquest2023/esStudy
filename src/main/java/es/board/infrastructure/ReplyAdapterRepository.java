package es.board.infrastructure;

import es.board.infrastructure.feed.ReplyAggView;
import es.board.infrastructure.jpa.ReplyJpaRepository;
import es.board.infrastructure.entity.feed.ReplyEntity;
import es.board.domain.ReplyRepository;
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
    public List<ReplyAggView> countRepliesIn(List<Integer> postIds) {
        return repository.countRepliesIn(postIds);
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
    public void deleteReply(long id) {
        repository.deleteById(id);
    }
}