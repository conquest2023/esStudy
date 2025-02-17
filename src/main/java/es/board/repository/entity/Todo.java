package es.board.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String userId;          // 해당 Todo가 어느 사용자의 것인지

    private String title;

    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;         // 마감일

    @Enumerated(EnumType.STRING)
    private TodoStatus status;    // 예: TODO, IN_PROGRESS, DONE

    private Integer priority;     // 우선순위 (1 높음, 5 낮음 등)

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // getters/setters
}
