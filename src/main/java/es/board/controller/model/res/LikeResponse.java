package es.board.controller.model.res;


import es.board.repository.document.Like;
import es.board.repository.entity.Likes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class LikeResponse {

    private  String  feedUID;


    private  String userId;

    private LocalDateTime localDateTime;




}
