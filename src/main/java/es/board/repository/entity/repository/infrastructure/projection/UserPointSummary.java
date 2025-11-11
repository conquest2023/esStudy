package es.board.repository.entity.repository.infrastructure.projection;

import lombok.Getter;

@Getter
public class UserPointSummary {

    private String userId;
    private int totalCount;

    public UserPointSummary(String userId, int totalCount) {
        this.userId = userId;
        this.totalCount = totalCount;
    }
}
