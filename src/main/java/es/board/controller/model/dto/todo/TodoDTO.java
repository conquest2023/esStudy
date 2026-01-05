package es.board.controller.model.dto.todo;

import es.board.repository.entity.TodoStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


public class TodoDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public  static class Response {

        private String userId;

        private String title;

        private String description;

        private String category;

        @Temporal(TemporalType.TIMESTAMP)
        private Date dueDate;

        @Enumerated(EnumType.STRING)
        private TodoStatus status;

        private Integer priority;

        private Boolean project;

        private LocalDateTime createdAt;

        private LocalDate end;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public  static class Request{


        private Long todo_id;


        private String title;
        private String description;

        @Temporal(TemporalType.TIMESTAMP)
        private LocalDate dueDate;

        @Enumerated(EnumType.STRING)
        private TodoStatus status;

        private  String category;

        private Integer priority;

        private LocalDateTime createdAt;

        private LocalDate end;

        private LocalDateTime updatedAt;
    }
}
