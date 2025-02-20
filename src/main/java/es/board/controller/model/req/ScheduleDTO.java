package es.board.controller.model.req;

import es.board.repository.entity.Schedule;
import lombok.*;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDTO {

    private Long scheduleId;
    private String userId;
    private String title;
    private LocalDateTime startDatetime;

    private LocalDateTime endDatetime;
    private Boolean allDay;
    private String location;

    private  String category;

    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 엔터티 -> DTO 변환 메서드

}
