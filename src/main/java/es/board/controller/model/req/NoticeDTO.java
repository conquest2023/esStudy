package es.board.controller.model.req;

import es.board.repository.entity.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class NoticeDTO {
    private Long id;


    private String  userId;
    private String title;

    private  String feedUID;
    private String description;

    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}