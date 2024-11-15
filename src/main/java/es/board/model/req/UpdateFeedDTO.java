package es.board.model.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFeedDTO {

    private String id;

    private String username;

    private String title;

    private String description;

    private  int likeCount;

    private LocalDate updatedAt;
}
