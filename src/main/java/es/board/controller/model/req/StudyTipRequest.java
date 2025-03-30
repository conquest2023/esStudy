package es.board.controller.model.req;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyTipRequest {

   private String title;

   private String link;

   private String image;

   private String description;
}
