package es.board.repository.entity.repository.infrastructure.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface VoteOptionProjection {

    long pollId();
    long sumValue();
}
