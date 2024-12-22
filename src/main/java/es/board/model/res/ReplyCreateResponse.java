package es.board.model.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyCreateResponse {


    private String id;

    private String commentUID;

    private String feedUID;

    private String username;


    private String content;

    private String category;

    private int likeCount;
}
