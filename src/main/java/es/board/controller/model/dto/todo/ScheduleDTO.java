package es.board.controller.model.dto.todo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.board.filter.annotation.XssSafeSerializer;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDTO {

    private Long scheduleId;

    private String userId;
    @JsonSerialize(using = XssSafeSerializer.class)
    private String title;

    private LocalDateTime startDatetime;

    private LocalDateTime endDatetime;


    @JsonFormat(pattern = "HH:mm")
    @JsonSerialize(using = XssSafeSerializer.class)
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    @JsonSerialize(using = XssSafeSerializer.class)
    private LocalTime endTime;

    private Boolean allDay;

    private String location;

    @JsonSerialize(using = XssSafeSerializer.class)
    private  String category;

    @JsonSerialize(using = XssSafeSerializer.class)
    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @JsonSerialize(using = XssSafeSerializer.class)
    private Boolean isRepeat;

    @JsonSerialize(using = XssSafeSerializer.class)
    private String repeatDays;

    @JsonSerialize(using = XssSafeSerializer.class)
    private LocalDateTime repeatStartDate;


    @JsonSerialize(using = XssSafeSerializer.class)
    private LocalDateTime repeatEndDate;

    @JsonSerialize(using = XssSafeSerializer.class)
    private String repeatFrequency;

    @JsonSerialize(using = XssSafeSerializer.class)
    private Integer repeatInterval;
}
