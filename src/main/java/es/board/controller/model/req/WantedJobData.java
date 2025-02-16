package es.board.controller.model.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WantedJobData {

    private String positionName;
    private String companyName;
    private String jobUrl;
    private String imgUrl;
    private String location;
}
