package es.board.infrastructure.jpa.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface VoteOptionProjection {

    long pollId();
    long sumValue();
}
