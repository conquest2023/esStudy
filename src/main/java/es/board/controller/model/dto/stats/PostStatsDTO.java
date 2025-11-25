package es.board.controller.model.dto.stats;

import lombok.Data;

@Data
public class PostStatsDTO {

    private final Integer postId;
    private final Long totalCommentCount;
    private final Long likeCount;
}
