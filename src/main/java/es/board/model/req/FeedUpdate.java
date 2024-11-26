package es.board.model.req;

import es.board.repository.document.Board;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedUpdate {

    private String feedUID;

    private String username;

    private String title;

    private String description;

    private  int likeCount;

    private LocalDate updatedAt;

    public Board updateFeed(String id, Board board , FeedUpdate update){
        String username = update.getUsername() != null ? update.getUsername() : board.getUsername();
        String title = update.getTitle() != null ? update.getTitle() : board.getTitle();
        String description = update.getDescription() != null ? update.getDescription() : board.getDescription();

        return Board.builder()
                .feedUID(id)
                .username(username)
                .title(title)
                .description(description)
                .likeCount(update.getLikeCount())
                .updatedAt(LocalDate.now())
                .build();
    }
}
