package es.board.model.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentSaveDTO {

    private String CommentUID;

    private String username;


    private String content;

    private  int LikeCount;

    private LocalDateTime createdAt;


}
