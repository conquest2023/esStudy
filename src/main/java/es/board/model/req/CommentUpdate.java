package es.board.model.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdate {
    private String CommentUID;

    private String username;

    private String content;

    private  int LikeCount;

    private LocalDateTime updatedAt;
}
