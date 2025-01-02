package es.board.controller.model.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewCountResponse {

    private String feedUID;

    private int  viewCount;




}
