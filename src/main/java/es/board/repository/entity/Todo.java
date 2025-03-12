package es.board.repository.entity;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name="todo")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Todo {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long todo_id;

    private String userId;
    private String title;

    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    @Enumerated(EnumType.STRING)
    private TodoStatus status;

    private String category;

    private Integer priority;

    private Boolean project;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDate end;
}
