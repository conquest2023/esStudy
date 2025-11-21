package es.board.repository.entity.repository.infrastructure.poll;

import es.board.repository.entity.poll.PollOptionEntity;

import java.util.Optional;

public interface PollOptionRepository {


    Optional<PollOptionEntity> isCheckOption(long pollId ,long optionId);
}
