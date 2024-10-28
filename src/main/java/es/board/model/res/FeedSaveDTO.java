package es.board.model.res;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedSaveDTO {


    private String id;

    private String username;


    private String title;


    private String description;
}
