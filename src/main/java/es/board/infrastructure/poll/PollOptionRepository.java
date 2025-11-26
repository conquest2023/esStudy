package es.board.infrastructure.poll;

import es.board.infrastructure.entity.poll.PollOptionEntity;

import java.util.List;
import java.util.Optional;

public interface PollOptionRepository {


    Optional<PollOptionEntity> isCheckOption(long pollId ,long optionId);

    Optional<List<PollOptionEntity>> isCheckOptionList(long pollId, List<Long> optionIds);
}
