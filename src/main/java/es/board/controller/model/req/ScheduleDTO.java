package es.board.controller.model.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.board.repository.entity.Schedule;
import lombok.*;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.time.LocalTime;

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

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private Boolean allDay;

    private String location;

    private  String category;

    private String description;
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean isRepeat; // 🔄 반복 일정 여부

    private String repeatDays; // 🔄 반복 요일 (예: "월,수,금")

    private LocalDateTime repeatStartDate; // 🔄 반복 일정 시작 날짜

    private LocalDateTime repeatEndDate; // 🔄 반복 일정 종료 날짜


    // 엔터티 -> DTO 변환 메서드

}
