package es.board.infrastructure.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface VoteOptionProjection {

    long pollId();
    long sumValue();
}
