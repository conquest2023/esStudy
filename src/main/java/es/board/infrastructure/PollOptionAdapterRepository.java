package es.board.infrastructure;

import es.board.infrastructure.entity.poll.PollOptionEntity;
import es.board.infrastructure.jpa.PollOptionJpaRepository;
import es.board.infrastructure.poll.PollOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PollOptionAdapterRepository implements PollOptionRepository {

    private  final PollOptionJpaRepository pollOptionJpaRepository;
    @Override
    public Optional<PollOptionEntity> isCheckOption(long pollId, long optionId) {
        return  pollOptionJpaRepository.isCheckOption(pollId,optionId);
    }



    @Override
    public Optional<List<PollOptionEntity>> isCheckOptionList(long pollId, List<Long> optionIds) {
        return  pollOptionJpaRepository.isCheckOptionList(pollId, optionIds);
    }

}
