package es.board.controller.model.dto.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobListing {
    private String title;
    private String company;
    private String url;

}
