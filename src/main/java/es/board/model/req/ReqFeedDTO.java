package es.board.model.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqFeedDTO {

    private String id;


    private String username;


    private String title;


    private String description;


}
