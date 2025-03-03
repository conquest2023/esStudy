package es.board.controller.model.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.board.config.XssSafeSerializer;
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

}
